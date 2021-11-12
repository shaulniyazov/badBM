package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.*;
import java.io.File;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DISCLAIMER: According to the readings I have done,
 * in order to check for the B in Right-BICEP, you
 * use the CORRECT acronym. Therefore, wherever it
 * mentions that it checks for CORRECT, I think it
 * also checks the B.
 */
class Tests {

    /**
     * Tests to see whether the randInt method in Util makes the random ints correctly.
     * In Right-BICEP, this is the Right because we are making sure that the
     * random integer that is produced is in the range it is supposed to be in
     * as set forth by the javadoc there.
     * This also checks for the first R in CORRECT, Range, because it is also
     * using a range to check for various cases.
     */

    @ParameterizedTest
    @CsvSource({"1,2","10,20","72,94"})
    void randInt(int min,int max) {
        boolean randIntWorked = false;
        int testNumber = Util.randInt(min,max);
        if(testNumber >= min && testNumber <= max ){
            randIntWorked = true;
        }
        assertTrue(randIntWorked);
    }

    /**
     * This test makes sure that the getDuration method of DiskRun will print
     * out "unknown" in the event that the endTime = null; as intended. This is
     * the E in Right-BICEP because making the getDuration not able to probably
     * do what it needs to do. This also tests of the first C in CORRECT,
     * Conformance. The expected answer is that the answer will be a String
     * outputting "unknown".
     * */
    @Test
    void getDuration(){
        DiskRun diskRun = new DiskRun();
        diskRun.setEndTime(null);
        String duration = diskRun.getDuration();
        assertEquals("unknown",duration);
    }

    /**
     * This test makes sure that the directory has been created using the
     * boolean return of setupDateArea and the File class exist() method.
     * This tests for the C in Right-BICEP in that it uses the File class
     * against the return value to make sure that the directory exists.
     * This also tests for the E in CORRECT in that it makes sure that the
     * directory actually exists.
     */
    @Test
    void setupDataArea(){
        Properties p = new Properties();
        String value = p.getProperty("locationDir", System.getProperty("user.home"));
        boolean isSetUp = false;

        App.locationDir = new File(value);
        App.dataDir = new File(App.locationDir.getAbsolutePath()
                + File.separator + App.DATADIRNAME);
        try {
            Method m = App.class.getDeclaredMethod("setupDataArea");
            m.setAccessible(true);
            App a  = new App();
            isSetUp = (boolean) m.invoke(a);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        assertEquals(isSetUp,App.dataDir.exists());
    }

    /**This test was written in order to break it, as-per
     * the assignment requirements. The displayString()
     * method was changed in order for it to return
     * a generic 00.00 for every value
     *
     */

    @Test
    void displayString() {
        String testString  = Util.displayString(12.6);
        assertEquals("12.6",testString);
    }

    /**This is a very simple test that makes sure that the
     * getEntityManager does not take more than 3 seconds.
     * this tests for the P in Right-BICEP, which makes sure
     * that it is within performance standard
     */

    @Test
    void getEntityManager() {
        assertTimeout(Duration.ofSeconds(3), ()->{
            EM.getEntityManager();
        } );
    }

}