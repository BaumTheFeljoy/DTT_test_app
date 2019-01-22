package com.example.RSRpechhulpTest.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.example.RSRpechhulpTest.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Manages the InfoWindow, InfoWindowContents and the position of the marker on the map
 */
public class MapManager {
    private final static float ZOOM_LEVEL = 16.0f;  //Default zoom level on launch

    private GoogleMap mMap;
    private Marker marker;
    private Context mContext;

    private String address = "default";
    private Boolean initialPositionSet = false;

    public MapManager(GoogleMap googleMap, Context context) {
        mMap = googleMap;
        mContext = context;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(context));

        initMarker();
    }

    /**
     * Adds marker with initial values to map but keeps it hidden
     */
    private void initMarker(){
        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(52, 4))
                .title("Uw Locatie:")
                .snippet(address + "\n" + mContext.getString(R.string.marker_text))
                .visible(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
        marker.setTag(0);
    }

    /**
     * Updates the marker with new Address information and position
     * @param latLng Latitude and Longitude of the new marker position
     */
    public void updateMarker(LatLng latLng){
        getAddress(latLng);

        marker.setVisible(true);
        marker.setSnippet(address + "\n" + mContext.getString(R.string.marker_text));
        marker.showInfoWindow();
        marker.setPosition(latLng);

        if(!initialPositionSet){//After the camera has been centered on the marker, future updates shouldn't move it again.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            initialPositionSet = true;
        }
    }

    /**
     * Calculates the address from LatLng coordinates using a Geocoder
     * @param latLng Latitude and Longitude of the address to be calculated
     */
    public void getAddress(LatLng latLng){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(mContext, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
