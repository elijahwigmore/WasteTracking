package com.wastetracking.wastetracking;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * Created by VR-Visitor on 3/29/2018.
 */

public class MapManagerFragment extends Fragment {

    public static final String TAG = "MapManagerFrag";

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        // Make sure this fragment is persisted
        this.setRetainInstance(true);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;
                //Init Home
                LatLng Home = null;
                //Init GeoCoder
                Geocoder gc = new Geocoder(getActivity().getApplicationContext());
                //Get list of addresses as strings
                ArrayList<String> addresses_list = ((MainActivity) getActivity()).getMissingAddressNames();
                ArrayList<String> addresses_collected_list = ((MainActivity) getActivity()).getCollectedAddressNames();
                //List for addresses
                ArrayList<Address> list = new ArrayList<Address>();
                ArrayList<Address> collected_list = new ArrayList<Address>();
                //Latlong for list
                LatLng temp_LL = null;

                try
                {
                    //Find and mark collected addresses
                    for (String address_string : addresses_collected_list) {
                        collected_list.add(gc.getFromLocationName(address_string, 1).get(0));
                        Log.d(TAG, "First collected loop: " + address_string);
                    }
                    for (Address gc_address : collected_list){
                        temp_LL = new LatLng(gc_address.getLatitude(), gc_address.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(temp_LL)
                                .title("Collected")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        );
                        Log.d(TAG, "First loop: " + gc_address.toString());
                    }

                    //Find and mark uncollected addresses
                    for (String address_string : addresses_list) {
                        Log.d(TAG, "Attempting address : " + address_string);

                        // Geocoders can sometimes fail to return an andress. Use the backup
                        ArrayList<com.wastetracking.wastetracking.Model.Address> possibleAddresses =
                                ((MainActivity) getActivity()).getObjFromAddressName(address_string);

                        // Just take the first one out of all possible addresses for now
                        try {
                            String lat = possibleAddresses.get(0).getLat();
                            String lon = possibleAddresses.get(0).getLon();

                            Locale current = getResources().getConfiguration().locale;
                            Address secondaryAddress = new Address(current);
                            secondaryAddress.setLatitude(Double.parseDouble(lat));
                            secondaryAddress.setLongitude(Double.parseDouble(lon));

                            list.add(secondaryAddress);
                        } catch (Exception e2){
                            Log.e(TAG, "Failed to resolve address!");
                            Log.e(TAG, e2.toString());
                        }

                    }
                    for (Address gc_address : list){
                        temp_LL = new LatLng(gc_address.getLatitude(), gc_address.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(temp_LL)
                                .title("Trash")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                        );
                        Log.d(TAG, "First loop: " + gc_address.toString());
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }

                // Add a marker in Sydney and move the camera
                LatLng Toronto = new LatLng(43.6532, -79.3832);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Toronto));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    // Show rationale and request permission.
                    Context context = getApplicationContext();
                    CharSequence text = "Location services disabled!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                Log.d(TAG, "Map fully loaded.");
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
