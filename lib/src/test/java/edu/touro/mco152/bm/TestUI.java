package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestUI implements UIMethods{

    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     *
     * @author lcmcohen
     */
    private void setupDefaultAsPerProperties()
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

    @Override
    @Test
    public boolean ifCancelled() {
        assertEquals(1,1);
        return false;
    }

    @Override
    @Test

    public void setCurrentProgress(int process) {
        boolean inRange;
        inRange = process <= 100 && process >= 0;
        assertTrue(inRange);

    }

    @Override
    @Test
    public void publishDiskMark(DiskMark mark) {
        assertEquals(1,1);
    }

    @Override
    @Test
    public boolean cancelWorker(boolean mayInterruptIfRunning) {
        assertEquals(1,1);
        return false;
    }

    @Override
    @Test
    public void addPCL(PropertyChangeListener listener) {
        assertEquals(1,1);
    }

    @Override
    @Test
    public void executeWorker() {
        assertEquals(1,1);
    }

    @Override
    @Test
    public void setDiskWorker(DiskWorker worker) {
        assertEquals(1,1);
    }

    @Override

    public boolean startDiskWorker() throws Exception {
        return App.worker.makeDiskWorker();
    }

    @Test
    public void start() throws Exception {
        setupDefaultAsPerProperties();
        System.out.println(App.dataDir);
        App.worker = new DiskWorker(new TestUI());
        App.worker.startWorker();

    }
}
