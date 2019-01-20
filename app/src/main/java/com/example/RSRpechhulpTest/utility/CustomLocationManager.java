package com.example.RSRpechhulpTest.utility;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Handles keeping track of the device location through initial position check getDeviceLocation()
 * and regular updates startLocationUpdates()
 */
public class CustomLocationManager {
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 sec */

    private MapManager mapManager;
    private Activity mActivity;

    private FusedLocationProviderClient mFusedLocationClient;

    private Boolean LocationUpdatesRunning = false;


    public CustomLocationManager(Activity activity, GoogleMap googleMap) {
        mActivity = activity;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);

        mapManager = new MapManager(googleMap, mActivity);
    }

    //Gets the most recent available location, saves it if the result is not null
    public void getDeviceLocation() {
        try {Task locationResult = mFusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(mActivity, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        saveLocation(task.getResult());

                    } else {
                        Log.d("log", "Current location is null. Using defaults.");
                        Log.e("log", "Exception: %s", task.getException());
                    }
                }
            });

            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage());
            }
    }

    //Save the most recent location and update marker on map
    private void saveLocation(Location location) {
        try {
            mapManager.updateMarker(new LatLng(location.getLatitude(), location.getLongitude()));
        } catch (NullPointerException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    //Trigger new location updates at interval
    //source: https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API
    //TODO: add stopLocationUpdates method and call in MapActivity at onPause()
    public void startLocationUpdates() {

        if (LocationUpdatesRunning) {//Making sure that there is only on LocationUpdate running at once
            return;
        } else {

            // Create the location request to start receiving updates
            //Location related
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

            // Create LocationSettingsRequest object using location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();

            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            SettingsClient settingsClient = LocationServices.getSettingsClient(mActivity);
            settingsClient.checkLocationSettings(locationSettingsRequest);


            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if(locationResult.getLastLocation() != null){
                                saveLocation(locationResult.getLastLocation());
                            }
                            LocationUpdatesRunning = true; //set to true once it has successfully started
                        }
                    },
                    Looper.myLooper());
        }
    }
}
