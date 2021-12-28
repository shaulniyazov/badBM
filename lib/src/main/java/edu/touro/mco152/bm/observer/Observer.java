package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.CommandInterface;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * This is an abstract class that the observers will implement
 */
public abstract class Observer {
    protected CommandInterface subjectCommand;
    public DiskRun diskRun;
    public abstract void update();
}