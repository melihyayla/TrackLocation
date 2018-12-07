package com.servet.tracklocation;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.servet.tracklocation.Helper.DatabaseHelper;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String address = "";
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseHelper = new DatabaseHelper(this.getApplicationContext());



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

        Intent intent = getIntent();

        double mLatitude = Double.parseDouble(intent.getStringExtra("Latitude"));
        double mLongitude = Double.parseDouble(intent.getStringExtra("Longitude"));
        address += intent.getStringExtra("Address");

        Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
        placeLocation.setLatitude(mLatitude);
        placeLocation.setLongitude(mLongitude);
        centerMapOnLocation(placeLocation, address);

    }


    public void centerMapOnLocation(Location location, String title){

        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.clear();
        if(title!= "Your Location"){
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        }

        ArrayList<HashMap<String,String>> locationList = databaseHelper.activeLocation();
        ArrayList<String> locations = new ArrayList<>();

        for (int i = 0; i < locationList.size(); i++) {
            locations.add(String.valueOf(locationList.get(i).get("id")));
        }

        ArrayList<String> mLocation = new ArrayList<>();
        //HashMap<String,String> mLocation = new HashMap<>();

        for(int i=0; i<locationList.size(); i++){
            String str = databaseHelper.locationDetail(Integer.parseInt(locations.get(i))).get("latitude")
                    + "-" + databaseHelper.locationDetail(Integer.parseInt(locations.get(i))).get("latitude")+ "-" +
                    databaseHelper.locationDetail(Integer.parseInt(locations.get(i))).get("location_title");

            if(!mLocation.contains(str))
            mLocation.add(str);



        }


        for (int i = 0; i < mLocation.size(); i++) {


            double latitude = Double.parseDouble(mLocation.get(i).split("-")[0]);
            double longitude = Double.parseDouble(mLocation.get(i).split("-")[1]);
            String mTitle = mLocation.get(i).split("-")[2];

            LatLng latLng = new LatLng(latitude,longitude);

            mMap.addMarker(new MarkerOptions().position(latLng).title(mTitle));

        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,18));
    }

}
