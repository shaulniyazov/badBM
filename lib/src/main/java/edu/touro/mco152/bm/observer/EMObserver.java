package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import jakarta.persistence.EntityManager;

/**
 * this is an observer for the EM class.
 */
public class EMObserver implements Observer {

    /**
     *Takes in a DiskRun and does all the needs of EM here, rather than in the Command.
     * @param diskRun
     */
    @Override
    public void update(DiskRun diskRun) {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(diskRun);
        em.getTransaction().commit();
    }
}
