package com.wastetracking.wastetracking;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.wastetracking.wastetracking.Model.RFIDScan;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Instrumented test for the realm object "RFIDScan"
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RFIDScanTest {
    @Test
    public void packageName_isValid() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.wastetracking.wastetracking", appContext.getPackageName());
    }

    @Test
    public void rfidScan_isNull() throws Exception {
        RFIDScan mockRFIDScan = new RFIDScan();
        assertNull(mockRFIDScan.getRFIDValue());
        assertNull(mockRFIDScan.getTimestamp());
    }

    @Test
    public void rfidScanFieldsValues_isValid() throws Exception {
        RFIDScan mockRFIDScan = new RFIDScan();

        String testRFIDValue = "ABC123";
        mockRFIDScan.setRFIDValue(testRFIDValue);
        assertEquals(mockRFIDScan.getRFIDValue(), testRFIDValue);

        Date testTimestamp = new Date();
        mockRFIDScan.setTimestamp(testTimestamp);
        assertEquals(mockRFIDScan.getTimestamp(), testTimestamp);
    }
}