package edu.touro.mco152.bm;


import edu.touro.mco152.bm.observer.Observer;
import edu.touro.mco152.bm.persist.DiskRun;

import edu.touro.mco152.bm.ui.Gui;

import java.io.File;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.App.msg;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;

public class ReadCommand implements CommandInterface {

    UIMethods userInterface;
    int numOfBlocks, numOfMarks, blockSizeKB;
    DiskRun.BlockSequence blockSequence;
    //todo diskRun, GUI
    private List<Observer> observers;

    /**
     *
     * @param userInterface
     * @param numOfBlocks
     * @param numOfMarks
     * @param blockSizeKB
     * @param blockSequence
     * Constructor takes in all these variables that are taken from App. Instead of getting it from App directly, it is passed in.
     * This allows for the variables to be set in any context chosen
     */
    ReadCommand(UIMethods userInterface, int numOfBlocks, int numOfMarks, int blockSizeKB, DiskRun.BlockSequence blockSequence){
        this.userInterface = userInterface;
        this.numOfBlocks = numOfBlocks;
        this.numOfMarks = numOfMarks;
        this.blockSizeKB = blockSizeKB;
        this.blockSequence = blockSequence;
        observers = new ArrayList<>();
    }
    public ReadCommand() {
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyAllObservers(DiskRun diskRun){
        for (Observer observer : observers) {
            observer.update(diskRun);
        }
    }

    /**
     * all it does is call the readBm method that does the read.
     */
    @Override
    public void execute() {
        readBm(userInterface,numOfBlocks,numOfMarks,blockSizeKB, blockSequence);
    }

    /**
     *
     * @param userInterface
     * @param numOfBlocks
     * @param numOfMarks
     * @param blockSizeKB
     * @param blockSequence
     * Takes in all the parameters that were defined in the constructor and uses them instead of the variables defined in App
     * This is the read program from DiskWorker
     */
    public void readBm(UIMethods userInterface, int numOfBlocks, int numOfMarks, int blockSizeKB, DiskRun.BlockSequence blockSequence){
        // declare local vars formerly in DiskWorker

        int wUnitsComplete = 0,
                rUnitsComplete = 0,
                unitsComplete;

        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

        int blockSize = blockSizeKB*KILOBYTE;
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

        notifyAllObservers(run);
    }

}
