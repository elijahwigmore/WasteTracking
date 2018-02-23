package com.wastetracking.wastetracking;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Manages all aspects of the device location - currently a stub that will only get an invalid location
 */

public class DeviceLocationManager {

    private static final String TAG = "DeviceLocationManager";
    private Context mContext;


    private LocationManager mLocationManager;

    public DeviceLocationManager(Context context){

        Log.d(TAG, "Starting device location manager.");
        mContext = context;

        // Commented out because not working

        // Set up Location Manager (Android class is also called Location Manager)
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();

        try {
            mLocationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        } catch (Exception e) {
            Log.e(TAG, "Failed to request location updates!");
            Log.e(TAG, e.toString());
        }

    }

    public String getLocationString(){
        Location curLoc;
        String latString = "Lat: NULL";
        String longString = "Long: NULL";

        try {
            curLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (curLoc == null) {
                curLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            latString = Double.toString(curLoc.getLatitude());
            longString = Double.toString(curLoc.getLongitude());
        } catch (Exception e) {
            Log.e(TAG, "Failed to get current Location");
            Log.e(TAG,  e.toString());
        }

        return latString + ", " + longString;

    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled");
        }
    }
}
