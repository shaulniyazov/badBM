package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.App.blockSequence;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class tests the Command and Invoker to make sure that they could work without DiskWorker.
 * It has 2 tests, one which tests the read command, and the other tests the write command.
 */
public class TestCommand {

    @BeforeAll
    private static void setupDefaultAsPerProperties()
    {
        /// Do the minimum of what  App.init() would do to allow it to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();
        System.out.println(App.getConfigString());
        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference
        App.dataDir = new File(App.locationDir.getAbsolutePath()+File.separator+App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        }
        else
        {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }

    @Test
    public void readCommandTest(){
        Invoker invoker = new Invoker();
        UIMethods userInterface = new TestUI();
        CommandInterface read = new ReadCommand(userInterface,128,25,2048, DiskRun.BlockSequence.SEQUENTIAL);
        invoker.setCommand(read);
    }

    @Test
    public void writeCommandTest(){
        Invoker invoker = new Invoker();
        UIMethods userInterface = new TestUI();
        CommandInterface write = new WriteCommand(userInterface,128,25,2048, DiskRun.BlockSequence.SEQUENTIAL);
        invoker.setCommand(write);
    }
}
