package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.CommandInterface;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;


public class GUIObserver extends Observer {

    public GUIObserver(CommandInterface subject, DiskRun diskRun){
        this.subjectCommand = subject;
        this.subjectCommand.registerObserver(this);
        this.diskRun = diskRun;
    }

    @Override
    public void update() {
        Gui.runPanel.addRun(diskRun);
    }
}

