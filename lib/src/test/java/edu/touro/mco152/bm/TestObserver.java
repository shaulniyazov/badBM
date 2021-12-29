package edu.touro.mco152.bm;

import edu.touro.mco152.bm.observer.Observer;
import static org.junit.jupiter.api.Assertions.*;

import edu.touro.mco152.bm.persist.DiskRun;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

/**
 * Test class that tests to make sure the observer is updating correctly
 */
public class TestObserver implements Observer {
    private static boolean hasBeenCalled = false;
    @Test
    public void test(){
        Observer testObserver = new TestObserver();
        testObserver.update(new DiskRun());
    }
    @Override
    public void update(DiskRun diskRun) {
        hasBeenCalled = true;
    }

    @AfterAll
    public static void testObserver(){
        assertEquals(true,hasBeenCalled);
    }
}
