package ru.netcracker.bikepacker.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.jenetics.jpx.GPX
import org.osmdroid.config.Configuration
import ru.netcracker.bikepacker.R
import ru.netcracker.bikepacker.tracks.UserTrack
import ru.netcracker.bikepacker.tracks.GpxUtil
import ru.netcracker.bikepacker.tracks.listeners.OnGpxCreatedListener
import java.util.stream.Collectors

class MainNavigationActivity : AppCompatActivity() {
    companion object {
        //to save current fragment on saveInstanceState
        const val CURRENT_FRAGMENT = "current_fragment"

        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
        private val settingsFragment = SettingsFragment()
        const val INITIAL_SELECTED_ITEM: Int = R.id.navigation_map

        //tag for each fragment
        const val TAG_MAP = "map"
        const val TAG_SETTINGS = "settings"
        const val TAG_RECORD = "record"
        const val TAG_HOME = "home"
        const val TAG_FINDFRIEND = "findFriend"

    }

    //fragments
    private val mapFragment: MapFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_MAP)
        if (fr != null) fr as MapFragment
        else MapFragment()
    }

    private val settingsFragment: SettingsFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_SETTINGS)
        if (fr != null) fr as SettingsFragment
        else SettingsFragment()
    }

    private val homeFragment: HomeFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_HOME)
        if (fr != null) fr as HomeFragment
        else{
            HomeFragment()
        }

    }

    private val findFriend: FindFriendFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_FINDFRIEND)
        if (fr != null) fr as FindFriendFragment
        else FindFriendFragment()
    }



    private val recordFragment: RecordFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_RECORD)

        if (fr != null) fr as RecordFragment
        else {
            val initialFr = RecordFragment()
            initialFr.setOnGpxCreatedListener(
                object : OnGpxCreatedListener {
                    override fun onGpxCreated(gpx: GPX?) {
                        val map = mapFragment.map
                        val userTrack: UserTrack = UserTrack.newInstance(
                            map,
                            mapFragment.startIcon,
                            mapFragment.finishIcon,
                            GpxUtil.trackToPolyline(
                                gpx?.tracks()?.collect(Collectors.toList())?.get(0)
                            )
                        )

                        map.let{
                            it.zoomToBoundingBox(userTrack.boundingBox,true)
                            it.overlayManager.addAll(userTrack.toList())
                        }
                        Log.d("Record track",map.overlayManager.overlays().toString());
                        Toast.makeText(ctx,map.overlayManager.overlays().toString(),Toast.LENGTH_LONG).show()

                    }
                }
            )
            initialFr
        }
    }

    private var ctx: Context? = null
    private var selectedFragment: Int = R.id.navigation_map
    private var activeFragment: Fragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = applicationContext

        //android preferences setting
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_main_navigation)
        //retrieve current fragment from savedInstanceState
        savedInstanceState?.let {
            selectedFragment = it.getInt(CURRENT_FRAGMENT, R.id.navigation_map)
        }

        //check the selected item of bottom menu and then show corresponding fragment
        when (selectedFragment) {
            R.id.navigation_map -> activeFragment = mapFragment
            R.id.navigation_settings -> activeFragment = settingsFragment
            R.id.navigation_home -> activeFragment = homeFragment
            R.id.find_friends_fragment -> activeFragment = findFriend
            //TODO: R.id.navigation_{required menu button} -> activeFragment = {required fragment}
        }

        if (savedInstanceState == null) {
            //add all fragments but show only
            //active fragment
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, mapFragment, TAG_MAP).hide(mapFragment)
                .add(R.id.fragment_container, settingsFragment, TAG_SETTINGS).hide(settingsFragment)
                .add(R.id.fragment_container, homeFragment, TAG_HOME).hide(homeFragment)
                .add(R.id.fragment_container, findFriend, TAG_FINDFRIEND).hide(findFriend)
                //TODO: .add(R.id.fragment_container, {required fragment}, TAG_RECORD).hide({required fragment})
                .show(activeFragment!!)
                .commit()
        }

        //bottom navigation view setting
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navig_view)
        bottomNav.selectedItemId = INITIAL_SELECTED_ITEM

        bottomNav.setOnItemSelectedListener {
            setFragment(it.itemId)
        }
    }

    private fun setFragment(itemId: Int): Boolean {
        selectedFragment = itemId

        when (itemId) {
            R.id.navigation_map -> {
                if (activeFragment is MapFragment) {
                    return if (supportFragmentManager.findFragmentByTag(
                            TAG_RECORD
                        ) == null
                    ) false
                    else {
                        supportFragmentManager
                            .beginTransaction()
                            .remove(recordFragment)
                            .show(mapFragment)
                            .commit()
                        true
                    }
                }
                supportFragmentManager.beginTransaction().hide(activeFragment!!).show(mapFragment)
                    .commit()
                activeFragment = mapFragment
            }
            R.id.navigation_record -> {
                if (activeFragment !is MapFragment) {
                    supportFragmentManager.beginTransaction().hide(activeFragment!!)
                        .show(mapFragment)
                        .commit()
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.start_new_route_container, recordFragment, TAG_RECORD)
                    .commit()
                mapFragment.setCenterOnGeoLocation()
                activeFragment = mapFragment
            }
            R.id.navigation_home ->{
                if (activeFragment is HomeFragment) return false
                val findFriendButton: ImageButton = findViewById(R.id.findFriendsButton)
                findFriendButton.setOnClickListener(object : View.OnClickListener{
                    override fun onClick(p0: View?) {
                        findFriend.disp()
                        supportFragmentManager
                            .beginTransaction()
                            .hide(activeFragment!!)
                            .show(findFriend).commit()

                        activeFragment = findFriend
                    }

                })
                supportFragmentManager
                    .beginTransaction()
                    .hide(activeFragment!!)
                    .show(homeFragment).commit()

                activeFragment = homeFragment
            }
            else -> {
                if (activeFragment is SettingsFragment) return false
                supportFragmentManager.beginTransaction().hide(activeFragment!!)
                    .show(settingsFragment).commit()

                activeFragment = settingsFragment
            }
        }
        return true
    }


    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_FRAGMENT, selectedFragment)
    }



}