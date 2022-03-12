package ru.netcracker.bikepacker.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Track
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.OverlayWithIW
import org.osmdroid.views.overlay.Polyline
import ru.netcracker.bikepacker.*
import ru.netcracker.bikepacker.databinding.ActivityMainNavigationBinding
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
        const val TAG_RECORD_SM = "record_summary"
    }

    private var downAnim: Animation? = null
    private var upAnim: Animation? = null
    private var userTrack: UserTrack? = null
    private var binding: ActivityMainNavigationBinding? = null

    //fragments
            private
    val recordSummaryFragment: RecordSummaryFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_RECORD_SM)
        if (fr != null) fr as RecordSummaryFragment
        else {
            val recordSummaryFragment_ = RecordSummaryFragment(recordFragment.trackRecorder)
            recordSummaryFragment_.setOnAcceptBtnClickListener {
                findViewById<BottomNavigationView>(R.id.bottom_navig_view).visibility = View.VISIBLE
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.up_alpha_trans, R.anim.down_alpha_trans)
                    .remove(recordSummaryFragment)
                    .commit()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.up_alpha_trans, R.anim.down_alpha_trans)
                    .replace(R.id.start_new_route_container, recordFragment, TAG_RECORD)
                    .commit()
                mapFragment.map.overlays.removeIf { overlay ->
                    overlay is OverlayWithIW &&
                            overlay.id == UserTrack.RECORDED_TRACK_TAG
                }
                Toast.makeText(
                    ctx,
                    mapFragment.map.overlayManager.overlays().toString(),
                    Toast.LENGTH_LONG
                ).show()
                findViewById<LinearLayout>(R.id.btn_container).let {
                    it.translationX = 0f
                    it.translationY = 0f
                    it.orientation = LinearLayout.VERTICAL
                    mapFragment.switchOnClickAnim()
                }
            }
            recordSummaryFragment_
        }
    }
    private val mapFragment: MapFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_MAP)
        if (fr != null) fr as MapFragment
        else ru.netcracker.bikepacker.view.MapFragment()
    }
    private val settingsFragment: SettingsFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_SETTINGS)
        if (fr != null) fr as SettingsFragment
        else SettingsFragment()
    }
    private val recordFragment: RecordFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_RECORD)

        if (fr != null) fr as RecordFragment
        else {
            val initialFr = ru.netcracker.bikepacker.view.RecordFragment()
            initialFr.setOnGpxCreatedListener { gpx ->
                run {
                    val map = mapFragment.map
                    userTrack = UserTrack.newInstance(
                        map,
                        mapFragment.startIcon,
                        mapFragment.finishIcon,
                        GpxUtil.trackToPolyline(
                            gpx?.tracks()?.collect(Collectors.toList())?.get(0)
                        )
                    )
                    Toast.makeText(ctx,
                        gpx?.tracks()?.collect(Collectors.toList())?.get(0)?.segments?.get(0)?.points?.get(0)?.description?.get(), Toast.LENGTH_LONG).show();
                    map.let {
                        it?.zoomToBoundingBox(userTrack?.boundingBox, true)
                        it?.overlayManager?.addAll(userTrack?.toList()!!)
                    }
                }
            }
            initialFr.setOnStopBtnClickListener {

                findViewById<FrameLayout>(R.id.start_new_route_container)?.startAnimation(downAnim)
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.up_alpha_trans, R.anim.down_alpha_trans)
                    .replace(R.id.record_summary_container, recordSummaryFragment, TAG_RECORD_SM)
                    .commit()

                binding!!.bottomNavigView.visibility = View.GONE
                findViewById<LinearLayout>(R.id.btn_container).orientation = LinearLayout.HORIZONTAL
                findViewById<LinearLayout>(R.id.btn_container).let {
                    it.translationX =
                        findViewById<FrameLayout>(R.id.start_new_route_container).paddingLeft - it.width - it.height / 2f
                    it.translationY =
                        binding!!.mainConstLayout.bottom - it.bottom + 0.0f
                    mapFragment.switchOnClickAnim()
                }
            }
            initialFr
        }
    }

    private var ctx: Context? = null
    private var selectedFragment: Int = R.id.navigation_map
    private var activeFragment: Fragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainNavigationBinding.inflate(layoutInflater)
        ctx = applicationContext
        downAnim = AnimationUtils.loadAnimation(ctx, R.anim.down_alpha_trans)
        upAnim = AnimationUtils.loadAnimation(ctx, R.anim.up_alpha_trans)
        with(downAnim) {
            this?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    supportFragmentManager
                        .beginTransaction()
                        .remove(recordFragment)
                        .show(mapFragment)
                        .commit()
                }
            })
        }

        //android preferences setting
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(binding!!.root)
        //retrieve current fragment from savedInstanceState
        savedInstanceState?.let {
            selectedFragment = it.getInt(CURRENT_FRAGMENT, R.id.navigation_map)
        }

        //check the selected item of bottom menu and then show corresponding fragment
        when (selectedFragment) {
            R.id.navigation_map -> activeFragment = mapFragment
            R.id.navigation_settings -> activeFragment = settingsFragment
            //TODO: R.id.navigation_{required menu button} -> activeFragment = {required fragment}
        }

        if (savedInstanceState == null) {
            //add all fragments but show only
            //active fragment
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, mapFragment, TAG_MAP).hide(mapFragment)
                .add(R.id.fragment_container, settingsFragment, TAG_SETTINGS).hide(settingsFragment)
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
                        findViewById<FrameLayout>(R.id.start_new_route_container)?.startAnimation(
                            downAnim
                        )
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
                    .setCustomAnimations(R.anim.up_alpha_trans, R.anim.down_alpha_trans)
                    .replace(R.id.start_new_route_container, recordFragment, TAG_RECORD)
                    .commit()
                mapFragment.mapController.animateTo(mapFragment.userLocation)
                activeFragment = mapFragment
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