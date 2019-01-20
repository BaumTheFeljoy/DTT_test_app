package com.example.RSRpechhulpTest.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.RSRpechhulpTest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Custom InfoWindowAdapter to show current location and address.
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mInfoWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mInfoWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }

    private void renderWindowText(Marker marker,View view){
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.tv_info_title);

        if(!title.equals("")){
           tvTitle.setText(title);
        }

        String address = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.tv_address);

        if(!address.equals("")){
            tvSnippet.setText(address);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mInfoWindow);
        return mInfoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mInfoWindow);
        return mInfoWindow;
    }
}
