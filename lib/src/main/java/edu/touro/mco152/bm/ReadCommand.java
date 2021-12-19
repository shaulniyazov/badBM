package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import edu.touro.mco152.bm.ui.Gui;
import jakarta.persistence.EntityManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.App.msg;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;

public class ReadCommand implements CommandInterface {

    UIMethods userInterface;
    int numOfBlocks, numOfMarks, blockSize;
    DiskRun.BlockSequence blockSequence;

    ReadCommand(UIMethods userInterface, int numOfBlocks, int numOfMarks, int blockSize, DiskRun.BlockSequence blockSequence){
        this.userInterface = userInterface;
        this.numOfBlocks = numOfBlocks;
        this.numOfMarks = numOfMarks;
        this.blockSize = blockSize;
        this.blockSequence = blockSequence;
    }

    ReadCommand(){}

    @Override
    public void execute() {
        readBm(userInterface,numOfBlocks,numOfMarks,blockSize, blockSequence);
    }

    public static void readBm(UIMethods userInterface, int numOfBlocks, int numOfMarks, int blockSize, DiskRun.BlockSequence blockSequence){
        // declare local vars formerly in DiskWorker

        int wUnitsComplete = 0,
                rUnitsComplete = 0,
                unitsComplete;

        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

       // int blockSize = blockSizeKb*KILOBYTE;
        byte [] blockArr = new byte [blockSize];
        for (int b=0; b<blockArr.length; b++) {
            if (b%2==0) {
                blockArr[b]=(byte)0xFF;
            }
        }

        DiskMark rMark;
        int startFileNum = App.nextMarkNumber;

        DiskRun run = new DiskRun(DiskRun.IOMode.READ, blockSequence);
        run.setNumMarks(numOfMarks);
        run.setNumBlocks(numOfBlocks);
        run.setBlockSize(blockSize);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));
        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        for (int m = startFileNum; m < startFileNum + App.numOfMarks && !userInterface.ifCancelled(); m++) {

            if (App.multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            rMark = new DiskMark(READ);
            rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, "r")) {
                for (int b = 0; b < numOfBlocks; b++) {
                    if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                        int rLoc = Util.randInt(0, numOfBlocks - 1);
                        rAccFile.seek((long) rLoc * blockSize);
                    } else {
                        rAccFile.seek((long) b * blockSize);
                    }
                    rAccFile.readFully(blockArr, 0, blockSize);
                    totalBytesReadInMark += blockSize;
                    rUnitsComplete++;
                    unitsComplete = rUnitsComplete + wUnitsComplete;
                    percentComplete = (float) unitsComplete / unitsTotal * 100f;
                    userInterface.setCurrentProgress((int) percentComplete);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);
            msg("m:" + m + " READ IO is " + rMark.getBwMbSec() + " MB/s    "
                    + "(MBread " + mbRead + " in " + sec + " sec)");
            App.updateMetrics(rMark);
            userInterface.publishDiskMark(rMark);

            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
            run.setEndTime(new Date());
        }

        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();

        Gui.runPanel.addRun(run);
    }
}
