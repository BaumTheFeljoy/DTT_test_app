package com.example.RSRpechhulpTest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.RSRpechhulpTest.utility.CustomLocationManager;
import com.example.RSRpechhulpTest.utility.MapLayoutManager;
import com.example.RSRpechhulpTest.utility.PermissionManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Keeps track of the devices current location and displays it on a map with address information
 * Can dial number for calling for user pickup
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Finals
    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final static int PERMISSIONS_REQUEST_CALL = 2;
    private final static int REQUEST_ENABLE_GPS = 1;
    private final static int REQUEST_ENABLE_WIFI = 2;
    private final static int REQUEST_ENABLE_DATA = 3;

    //GMap related
    private GoogleMap map;
    private MapLayoutManager mapLayoutManager;
    private CustomLocationManager customLocationManager;

    //Permissions
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Init manager classes
        permissionManager = new PermissionManager(this);
        mapLayoutManager = new MapLayoutManager(this);

        //Replace ActionBar with Toolbar with back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Init CustomLocationManager
        customLocationManager = new CustomLocationManager(this, map);
        customLocationManager.startLocationUpdates();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //Making sure that GPS and Internet is always available while trying to not create multiple popups at once
        if (!isLocationEnabled())return;
        else if(!isNetworkAvailable())return;
        else {
            permissionManager.getLocationPermission();

            if(permissionManager.locationPermissionGranted) {
                try{//onResume is called before onMapReady at launch.
                    customLocationManager.getDeviceLocation();
                    customLocationManager.startLocationUpdates();
                }catch ( NullPointerException e){
                    Log.e("Exception: %s", e.getMessage());
                }
            }
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()){
            buildAlertMessageNoNetwork();
            return false;
        }
        return true;
    }

    /**
     * Displays alert message in case the GPS is turned off.
     * Has options to go back or change setting
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.gps_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.activate), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, REQUEST_ENABLE_GPS);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displays alert message in case the Internet is turned off.
     * Has options to go back, to WIFI or to DATA settings.
     */
    private void buildAlertMessageNoNetwork() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.no_network_message))
                .setTitle(getString(R.string.network_off))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.open_wifi_settings), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivityForResult(enableGpsIntent, REQUEST_ENABLE_WIFI);
                    }
                })
                .setNegativeButton(getString(R.string.open_data_settigns),new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                        startActivityForResult(enableGpsIntent, REQUEST_ENABLE_DATA);
                    }
                })
                .setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case REQUEST_ENABLE_GPS:{
                if(permissionManager.locationPermissionGranted){//Check the result
                    customLocationManager.getDeviceLocation();
                    customLocationManager.startLocationUpdates();
                }else{
                    permissionManager.getLocationPermission();
                }
                break;
            }case REQUEST_ENABLE_WIFI:{
                isNetworkAvailable();
                break;
            }case REQUEST_ENABLE_DATA:{
                isNetworkAvailable();
                break;
            }
        }
    }

    /**
     * Used to open and close the popupWrapper
     * @param view
     */
    public void buttonPressed(View view){
        mapLayoutManager.changePopupWrapper(view);
    }

    /**
     * Checks permission and dials the number instead of calling right away because it is less intrusive.
     * @param view
     */
    public void dialNumber(View view){
        if(!permissionManager.callPermissionGranted){
            permissionManager.getCallPermission();
        }else{
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(getString(R.string.rsr_number)));
            startActivity(intent);
        }
    }

    /**
     * Called after permission dialog, change permission fields according to choice
     * @param requestCode to call the right switch case
     * @param permissions the permissions that were requested
     * @param grantResults results of the permission Requests
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                permissionManager.setLocationPermission(false);
                // If request is somehow cancelled, the array is empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionManager.setLocationPermission(true);
                }
                break;
            }case PERMISSIONS_REQUEST_CALL:{
                permissionManager.setCallPermission(false);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionManager.setCallPermission(true);
                    dialNumber(findViewById(R.id.map));
                }
                break;
            }
        }
    }
}
