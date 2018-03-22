package com.wastetracking.wastetracking;

/**
 * Created by jdavi on 3/22/2018.
 */

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//JD
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.widget.Toast;

public class MapManager extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng Home = null;

        Geocoder gc = new Geocoder(MapManager.this);
        //Address address = null;
        //if( true )
        //{
        try
        {
            List<Address> list = gc.getFromLocationName("16 Criscoe St, York, ON", 1);
            //List<Address> list = gc.getFromLocationName(“155 Park Theater, Palo Alto, CA”, 1);
            Address address = list.get(0);
            Home = new LatLng(address.getLatitude(), address.getLongitude());
        } catch (IOException e){
            e.printStackTrace();
        }

        // Add a marker in Sydney and move the camera
        LatLng Toronto = new LatLng(43.6532, -79.3832);
        mMap.addMarker(new MarkerOptions().position(Home).title("Home?"));
        mMap.addMarker(new MarkerOptions().position(Toronto).title("Marker in Toronto"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Toronto));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

}
