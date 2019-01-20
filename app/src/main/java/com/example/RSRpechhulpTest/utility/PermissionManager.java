package com.example.RSRpechhulpTest.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Handles checking and acquiring of permissions for the MapActivity
 */
public class PermissionManager extends AppCompatActivity {
    //Finals
    private final static String[] PERMISSIONS_LOC = {Manifest.permission.ACCESS_FINE_LOCATION};
    private final static String[] PERMISSIONS_CALL = {Manifest.permission.CALL_PHONE};
    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final static int PERMISSIONS_REQUEST_CALL = 2;

    //Permission status
    public Boolean callPermissonGranted = false;
    public Boolean locationPermissionGranted = false;

    Context mContext;
    Activity mActivity;

    //Gets Context and initializes permissions accordingly
    public PermissionManager(Context context) {
        mContext = context;
        initPermissions();
    }

    //Set permissions according to previous given permissions
    public void initPermissions(){
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)callPermissonGranted = true;
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)locationPermissionGranted = true;
    }

    public void getCallPermission(){
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            callPermissonGranted = true;
        }else{//not granted, open permission dialog
            ActivityCompat.requestPermissions((Activity)mContext,PERMISSIONS_CALL, PERMISSIONS_REQUEST_CALL);
        }
    }

    public void getLocationPermission(){
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = true;
        }else {//not granted, open permission dialog
            ActivityCompat.requestPermissions((Activity)mContext,PERMISSIONS_LOC,PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //TODO: Fix this not being called after getting permissions
    //Called after permission dialog, change permission fields according to choice
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                locationPermissionGranted = false;
                // If request is somehow cancelled, the array is empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted  = true;
                }
                break;
            }case PERMISSIONS_REQUEST_CALL:{
                callPermissonGranted = false;
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPermissonGranted  = true;
                }
                break;
            }
        }
    }
}
