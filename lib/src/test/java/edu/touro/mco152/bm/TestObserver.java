package edu.touro.mco152.bm;

import edu.touro.mco152.bm.observer.Observer;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

/**
 * Test class that tests to make sure the observer is updating correctly
 */
public class TestObserver extends Observer {
    private static boolean hasBeenCalled = false;
    @Test
    public void test(){
        Observer testObserver = new TestObserver();
        testObserver.update();
    }
    @Override
    public void update() {
        hasBeenCalled = true;
    }

    @AfterAll
    public static void testObserver(){
        assertEquals(true,hasBeenCalled);
    }
}
