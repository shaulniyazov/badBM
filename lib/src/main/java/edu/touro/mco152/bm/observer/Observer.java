package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.CommandInterface;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * This is an abstract class that the observers will implement
 */
public interface Observer {
    void update(DiskRun diskRun);
}