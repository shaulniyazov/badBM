package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.CommandInterface;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import jakarta.persistence.EntityManager;

public class EMObserver extends Observer {

    public EMObserver(CommandInterface subject, DiskRun diskRun){
        this.subjectCommand = subject;
        this.subjectCommand.registerObserver(this);
        this.diskRun = diskRun;
    }

    @Override
    public void update() {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(diskRun);
        em.getTransaction().commit();
    }
}
