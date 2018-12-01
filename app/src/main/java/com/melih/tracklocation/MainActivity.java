package com.melih.tracklocation;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.melih.tracklocation.Helper.DatabaseHelper;
import com.melih.tracklocation.Services.LocationMonitoringService;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    private boolean mAlreadyStartedService = false;
    private TextView mMsgView;
    private ListView listView;
    DatabaseHelper databaseHelper;
    static boolean flag=true;
    String intentStr;
    ArrayList<String> arrayList;
    ArrayAdapter arrayAdapter;
    ArrayList<String> latitudeArray;
    ArrayList<String> longitudeArray;
    String finalStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMsgView = findViewById(R.id.loading_text);
        listView = findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        latitudeArray = new ArrayList<>();
        longitudeArray = new ArrayList<>();

        listView.setAdapter(arrayAdapter);

        mMsgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogBox();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                intent.putExtra("Latitude", latitudeArray.get(position));
                intent.putExtra("Longitude", longitudeArray.get(position));

                startActivity(intent);

            }
        });


        if(intentStr==null){
            Log.i("IntentStr", " NULL ");

            SimpleDateFormat timeSdf = new SimpleDateFormat("dd-MM-yyyy");

            intentStr = timeSdf.format(new Date());

            Log.i("IntentStr", intentStr);
        }

        finalStr = intentStr;

        /*ArrayList<HashMap<String,String>> locationList = databaseHelper.activeLocation();
        ArrayList<String> locations = new ArrayList<>();
        ArrayList<String> details = new ArrayList<>();

        intentStr=null;

        if(intentStr==null){
            Log.i("IntentStr", " NULL ");

            SimpleDateFormat timeSdf = new SimpleDateFormat("dd-MM-yyyy");

            intentStr = timeSdf.format(new Date());

            Log.i("IntentStr", intentStr);
        }

        final String finalStr = intentStr;

        for (int i = 0; i < locationList.size(); i++) {
            locations.add(String.valueOf(locationList.get(i).get("id")));
        }

        ArrayList<String> mLatitudeArray = new ArrayList<>();
        ArrayList<String> mLongitudeArray = new ArrayList<>();

        if(intentStr!=null){

        for (int j = 0; j < locationList.size(); j++) {
            if(databaseHelper.locationDetail(Integer.parseInt(locations.get(j))).get("location_date").equals(intentStr)){

                String listString = databaseHelper.locationDetail(Integer.parseInt(locations.get(j))).get("location_title");
                mLatitudeArray.add(databaseHelper.locationDetail(Integer.parseInt(locations.get(j))).get("latitude"));
                mLongitudeArray.add(databaseHelper.locationDetail(Integer.parseInt(locations.get(j))).get("longitude"));
                details.add(listString);


            }
        }
        }*/

        refresh(finalStr);


        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        //arrayList.clear();
        //latitudeArray.clear();
        //longitudeArray.clear();
        /*locationList.clear();

        if(details!=null){

            arrayList.clear();
            latitudeArray.clear();
            longitudeArray.clear();

            arrayList.addAll(details);
            arrayAdapter.notifyDataSetChanged();

            latitudeArray.addAll(mLatitudeArray);
            longitudeArray.addAll(mLongitudeArray);

            details.clear();
            mLatitudeArray.clear();
            mLongitudeArray.clear();
            //arrayList.clear();
        }*/


        if(flag){
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);
                        String timeStr = "";
                        String dateStr = "";
                        if (latitude != null && longitude != null) {
                            //String str = "Latitude : " + latitude + " Longitude: " + longitude;

                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            String address = "";
                            try {
                                List<Address> listAddresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                                if (listAddresses != null && listAddresses.size() > 0) {
                                    if (listAddresses.get(0).getThoroughfare() != null) {
                                        if (listAddresses.get(0).getSubThoroughfare() != null) {
                                            address += listAddresses.get(0).getSubThoroughfare() + " ";

                                        }
                                        address += listAddresses.get(0).getThoroughfare();
                                        SimpleDateFormat dateSdf = new SimpleDateFormat("dd-MM-yyyy");
                                        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
                                        timeStr = timeSdf.format(new Date());
                                        dateStr = dateSdf.format(new Date());

                                        address += " - " + timeStr + " - " + dateStr;
                                    }

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (address == "") {

                                SimpleDateFormat dateSdf = new SimpleDateFormat("dd-MM-yyyy");
                                SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
                                timeStr = timeSdf.format(new Date());
                                dateStr = dateSdf.format(new Date());
                                address += "Yer ismi yok - " + timeStr + " - " + dateStr;

                            }

                            if(finalStr.equals(dateStr)){

                                arrayList.add(address);
                                arrayAdapter.notifyDataSetChanged();
                                latitudeArray.add(latitude);
                                longitudeArray.add(longitude);
                            }

                            databaseHelper.addLocation(address,longitude,latitude,timeStr,dateStr);
                            //mMsgView.setText("Location Tracking Started");
                            //Log.i("LocationTrack", str);
                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

        flag=false;
        }


    }


    public void refresh(String str){


        ArrayList<HashMap<String,String>> locationList = databaseHelper.activeLocation();
        ArrayList<String> locations = new ArrayList<>();
        ArrayList<String> details = new ArrayList<>();

        intentStr=str;

        if(intentStr==null){
            Log.i("IntentStr", "NULL");

            SimpleDateFormat timeSdf = new SimpleDateFormat("dd-MM-yyyy");

            intentStr = timeSdf.format(new Date());

            Log.i("IntentStr", intentStr);
        }


        for (int i = 0; i < locationList.size(); i++) {
            locations.add(String.valueOf(locationList.get(i).get("id")));
        }

        ArrayList<String> mLatitudeArray = new ArrayList<>();
        ArrayList<String> mLongitudeArray = new ArrayList<>();

        if(intentStr!=null){

            for (int j = 0; j < locationList.size(); j++) {
                if(databaseHelper.locationDetail(Integer.parseInt(locations.get(j))).get("location_date").equals(intentStr)){

                    String listString = databaseHelper.locationDetail(Integer.parseInt(locations.get(j))).get("location_title");
                    mLatitudeArray.add(databaseHelper.locationDetail(Integer.parseInt(locations.get(j))).get("latitude"));
                    mLongitudeArray.add(databaseHelper.locationDetail(Integer.parseInt(locations.get(j))).get("longitude"));
                    details.add(listString);


                }
            }
        }

        locationList.clear();

        if(details!=null){

            arrayList.clear();
            latitudeArray.clear();
            longitudeArray.clear();

            arrayList.addAll(details);
            arrayAdapter.notifyDataSetChanged();

            latitudeArray.addAll(mLatitudeArray);
            longitudeArray.addAll(mLongitudeArray);

            details.clear();
            mLatitudeArray.clear();
            mLongitudeArray.clear();
            //arrayList.clear();
        }



    }


    public void createDialogBox(){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_box,null);

        builder.setView(mView);
        final android.app.AlertDialog dialog = builder.create();

        ListView listView2 = mView.findViewById(R.id.date_list);


        ArrayList<String> locations = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        final ArrayList<String> datesStr = new ArrayList<>();

        ArrayList<String> arrayList;

        ArrayList<HashMap<String,String>> locationList = databaseHelper.activeLocation();

        for (int i = 0; i < locationList.size(); i++) {
            locations.add(String.valueOf(locationList.get(i).get("id")));

        }

        for (int i = 0; i < locationList.size(); i++) {

            dates.add(databaseHelper.locationDetail(Integer.parseInt(locations.get(i))).get("location_date"));
            if(!datesStr.contains(dates.get(i))){
                datesStr.add(dates.get(i));
            }
        }

        arrayList = datesStr;

        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView2.setAdapter(arrayAdapter2);

        arrayAdapter2.notifyDataSetChanged();

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = datesStr.get(position);

                refresh(str);

                dialog.dismiss();
            }
        });


        dialog.show();

    }




    @Override
    public void onResume() {
        super.onResume();

        startStep1();
    }

    /**
     * Step 1: Check Google Play services
     */
    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            startStep2(null);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService && mMsgView != null) {

            mMsgView.setText(R.string.msg_location_service_started);

            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    @Override
    public void onDestroy() {


        //Stop location sharing service to app server.........

        stopService(new Intent(this, LocationMonitoringService.class));
        mAlreadyStartedService = false;
        //Ends................................................


        super.onDestroy();
    }






}