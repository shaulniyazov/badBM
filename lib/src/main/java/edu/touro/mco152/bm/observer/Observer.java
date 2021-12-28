package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.CommandInterface;
import edu.touro.mco152.bm.DiskWorker;

/**
 * This is an abstract class that the observers will implement
 */
public abstract class Observer {
    protected CommandInterface subjectCommand;
    public abstract void update();
}