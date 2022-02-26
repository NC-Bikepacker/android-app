package ru.netcracker.bikepacker;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;

public class MainNavigationActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final Fragment INITIAL_FRAGMENT = new MapFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();

        //android preferences setting
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main_navigation);

        //bottom navigation view setting
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnItemSelectedListener(item -> {


            Fragment centerFragment;
            if (item.getItemId() == R.id.navigation_map) {
                centerFragment = new MapFragment();
            } else if(item.getItemId() == R.id.navigation_settings) {
                centerFragment = new SettingsFragment();
            } else if(item.getItemId() == R.id.navigation_home) {
                centerFragment = new HomeFragment();
            } else if(item.getItemId() == R.id.navigation_account) {
                centerFragment = new UserMenuFragment();
            } else {
                //TODO: New fragments
                centerFragment = new SettingsFragment();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.center_fragment_container, centerFragment)
                    .commit();
            return true;
        });

        //requesting permissions if they'rnt requested or accepted
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
        //setting initial fragment in fragment container
        getSupportFragmentManager().beginTransaction().replace(R.id.center_fragment_container, INITIAL_FRAGMENT).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String[] permissionsToRequest = permissions.clone();
        if (permissionsToRequest.length > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest,
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

}