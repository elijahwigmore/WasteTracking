package com.wastetracking.wastetracking;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tony on 11/8/2017.
 *
 * This class acts as a general utility library, and all methods should be static.
 */

public final class Utility {

    private static String LOG_TAG = "Utility";

    // Prevent instance creation
    private Utility() {

    }

    private static String dateFormatPattern = "yyyy-MM-dd HH:mm:ss";
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
            return dateFormat.format(new Date());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }

        return dateFormatPattern;
    }
}
