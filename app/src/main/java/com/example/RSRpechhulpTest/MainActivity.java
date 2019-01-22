package com.example.RSRpechhulpTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    float dpWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        determineDisplayWidth();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Replace ActionBar with Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
    }

    /**
     * Calculate dpWidth of the device screen
     */
    private void determineDisplayWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        dpWidth = outMetrics.widthPixels / density;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(dpWidth>=600) return false;// don't enable the i-button on the toolbar on devices with dpWidth>=600
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    /**
     * Toolbar options selected method with easy expandability
     * @param item The selected item
     * @return boolean depending on success of call
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.info_btn:
                Intent intent = new Intent(this, AboutPageActivity.class);
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void openMapsActivity(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void openAboutActivity(View view){
        Intent intent = new Intent(this, AboutPageActivity.class);
        startActivity(intent);
    }
}
