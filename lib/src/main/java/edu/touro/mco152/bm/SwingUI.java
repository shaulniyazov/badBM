package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import edu.touro.mco152.bm.ui.Gui;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;

public class SwingUI extends SwingWorker<Boolean, DiskMark> implements UIMethods {
    DiskWorker diskWorker;

    @Override
    public void setDiskWorker(DiskWorker diskWorker) {
        this.diskWorker = diskWorker;
    }

    @Override
    public boolean startDiskWorker() throws Exception {
        return  doInBackground();
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return diskWorker.makeDiskWorker();
    }

    /**
     * Process a list of 'chunks' that have been processed, ie that our thread has previously
     * published to Swing. For my info, watch Professor Cohen's video -
     * Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
     * @param markList a list of DiskMark objects reflecting some completed benchmarks
     */
    @Override
    protected void process(List<DiskMark> markList) {
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }

    @Override
    protected void done() {
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }

    @Override
    public boolean ifCancelled() {
        return super.isCancelled();
    }

    @Override
    public void setCurrentProgress(int progress) {
        super.setProgress(progress);
    }

    @Override
    public void publishDiskMark(DiskMark mark) {
        super.publish(mark);
    }

    @Override
    public boolean cancelWorker(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    public void addPCL(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
    }

    @Override
    public void executeWorker() {
        super.execute();
    }
}
