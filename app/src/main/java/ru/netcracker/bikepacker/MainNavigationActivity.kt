package ru.netcracker.bikepacker

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import ru.netcracker.bikepacker.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import ru.netcracker.bikepacker.MapFragment
import ru.netcracker.bikepacker.SettingsFragment
import ru.netcracker.bikepacker.HomeFragment
import ru.netcracker.bikepacker.MainNavigationActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.view.MenuItem
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import java.util.ArrayList

class MainNavigationActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext

        //android preferences setting
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_main_navigation)

        //bottom navigation view setting
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav.setOnItemSelectedListener { item: MenuItem ->
            val centerFragment: Fragment
            centerFragment = if (item.itemId == R.id.navigation_map) {
                MapFragment()
            } else if (item.itemId == R.id.navigation_settings) {
                SettingsFragment()
            } else if (item.itemId == R.id.navigation_home) {
                HomeFragment()
            } else {
                //TODO: New fragments
                SettingsFragment()
            }
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.center_fragment_container, centerFragment)
                    .commit()
            true
        }

        //requesting permissions if they'rnt requested or accepted
        requestPermissionsIfNecessary(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
        //setting initial fragment in fragment container
        supportFragmentManager.beginTransaction().replace(R.id.center_fragment_container, INITIAL_FRAGMENT).commit()
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = permissions.clone()
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest,
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toTypedArray(),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
        private val INITIAL_FRAGMENT: Fragment = MapFragment()
    }
}