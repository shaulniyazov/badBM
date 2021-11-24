package edu.touro.mco152.bm;

import edu.touro.mco152.bm.DiskMark;
import java.beans.PropertyChangeListener;

public interface UIMethods {

    //isCancelled
    boolean ifCancelled();

    //setProgress
    void setCurrentProgress(int progress);

    //publish
    void publishDiskMark(DiskMark mark);

    //cancel
    boolean cancelWorker(boolean mayInterruptIfRunning);

    //addPropertyChangeListener
    void addPCL(PropertyChangeListener listener);

    //execute
    void executeWorker();

    void setDiskWorker(DiskWorker worker);

    boolean startDiskWorker() throws Exception;
}
