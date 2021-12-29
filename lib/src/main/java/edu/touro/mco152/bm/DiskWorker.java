package edu.touro.mco152.bm;

import edu.touro.mco152.bm.observer.EMObserver;

import edu.touro.mco152.bm.observer.SlackManager;
import edu.touro.mco152.bm.ui.Gui;
import javax.swing.*;
import static edu.touro.mco152.bm.App.*;

/**
 * Run the disk benchmarking as a Swing-compliant thread (only one of these threads can run at
 * once.) Cooperates with Swing to provide and make use of interim and final progress and
 * information, which is also recorded as needed to the persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks. It is instantiated by the
 * startBenchmark() method.
 * <p>
 * To be Swing compliant this class extends SwingWorker and declares that its final return (when
 * doInBackground() is finished) is of type Boolean, and declares that intermediate results are communicated to
 * Swing using an instance of the DiskMark class.
 */

public class DiskWorker{
    /* TODO
    make an interface with all these methods that are now red
    the red shows what now needs to be  worked on
    change the names of those new methods
    there will be 2 implementations: swing and a unit test
    MySwing extends SwingWorker implements NewInterface
    MySwing will have the same implementation
    error that could happen: a thread problem
    MySwing will have a reference to DiskWorker
         when execute is called, it calls doInBackground, which then needs to call the DiskWorker methods
     */
    UIMethods userInterface;

    DiskWorker(UIMethods userInterface){
        this.userInterface = userInterface;
    }

    public boolean startWorker() throws Exception {
        return userInterface.startDiskWorker();
    }

    protected Boolean makeDiskWorker() throws Exception {
        Invoker invoker = new Invoker();
        CommandInterface write = new WriteCommand(userInterface,numOfBlocks,numOfMarks,blockSizeKb,blockSequence);
        CommandInterface read = new ReadCommand(userInterface, numOfBlocks, numOfMarks, blockSizeKb, blockSequence);

        write.registerObserver(new EMObserver());
        write.registerObserver(new Gui());
        //write.registerObserver(new SlackManager("BadBM"));

        read.registerObserver(new EMObserver());
        read.registerObserver(new Gui());
        //read.registerObserver(new SlackManager("BadBM"));
        //read slack observer
//the client is the one that registers the observers to the subject

            /*
      We 'got here' because: a) End-user clicked 'Start' on the benchmark UI,
      which triggered the start-benchmark event associated with the App::startBenchmark()
      method.  b) startBenchmark() then instantiated a DiskWorker, and called
      its (super class's) execute() method, causing Swing to eventually
      call this doInBackground() method.
     */
        System.out.println("*** starting new worker thread");
        msg("Running readTest " + App.readTest + "   writeTest " + App.writeTest);
        //todo remove these variables from the commands
        msg("num files: " + App.numOfMarks + ", num blks: " + App.numOfBlocks
                + ", blk size (kb): " + App.blockSizeKb + ", blockSequence: " + App.blockSequence);

        /*
          init local vars that keep track of benchmarks, and a large read/write buffer
         */
        int blockSize = blockSizeKb * KILOBYTE;
        byte[] blockArr = new byte[blockSize];
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte) 0xFF;
            }
        }

        Gui.updateLegend();  // init chart legend info

        if (App.autoReset) {
            App.resetTestData();
            Gui.resetTestData();
        }

        int startFileNum = App.nextMarkNumber;

        /*
          The GUI allows either a write, read, or both types of BMs to be started. They are done serially.
         */
        if (App.writeTest) {
            //write
            invoker.setCommand(write);
            invoker.callCommand();
        }

        /*
          Most benchmarking systems will try to do some cleanup in between 2 benchmark operations to
          make it more 'fair'. For example a networking benchmark might close and re-open sockets,
          a memory benchmark might clear or invalidate the Op Systems TLB or other caches, etc.
         */

        // try renaming all files to clear catch
        if (App.readTest && App.writeTest && !userInterface.ifCancelled()) {
            JOptionPane.showMessageDialog(Gui.mainFrame,
                    "For valid READ measurements please clear the disk cache by\n" +
                            "using the included RAMMap.exe or flushmem.exe utilities.\n" +
                            "Removable drives can be disconnected and reconnected.\n" +
                            "For system drives use the WRITE and READ operations \n" +
                            "independantly by doing a cold reboot after the WRITE",
                    "Clear Disk Cache Now", JOptionPane.PLAIN_MESSAGE);
        }

        // Same as above, just for Read operations instead of Writes.
        if (App.readTest) {
            //read
            invoker.setCommand(read);
            invoker.callCommand();
        }

        App.nextMarkNumber += App.numOfMarks;

        return true;
    }



}
