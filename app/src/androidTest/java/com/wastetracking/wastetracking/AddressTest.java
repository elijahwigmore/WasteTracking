package com.wastetracking.wastetracking;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.wastetracking.wastetracking.Model.Address;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test for the realm object "Address"
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AddressTest {
    @Test
    public void packageName_isValid() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.wastetracking.wastetracking", appContext.getPackageName());

    }

    @Test
    public void address_isNull() throws Exception {
        Address mockAddress = new Address();
        assertNull(mockAddress.getAddress());
        assertNull(mockAddress.getLat());
        assertNull(mockAddress.getLon());
        assertNull(mockAddress.getRFIDValue());
        assertNull(mockAddress.getRealm());
    }

    @Test
    public void addressFieldsValues_isValid() throws Exception {
        Address mockAddress = new Address();
        mockAddress.setAddress("Test Address");
        assertEquals(mockAddress.getAddress(), "Test Address");

        // Use sample location 43.6547218,-79.3923236, Queen's Park lawn
        mockAddress.setLat("43.6547218");
        mockAddress.setLon("-79.3923236");
        assertEquals(mockAddress.getLat(), "43.6547218");
        assertEquals(mockAddress.getLon(), "-79.3923236");
        assertEquals(Double.valueOf(mockAddress.getLat()), 43.6547218, 0);
        assertEquals(Double.valueOf(mockAddress.getLon()), -79.3923236, 0);
    }
}