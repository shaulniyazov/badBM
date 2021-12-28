package edu.touro.mco152.bm;

import edu.touro.mco152.bm.observer.Observer;

/**
 * This is the Interface of the Commands used in DiskWorker
 */
public interface CommandInterface {

    void execute();

    public void registerObserver(Observer observer);

    public void unregisterObserver(Observer observer);

    public void notifyAllObservers();
}
