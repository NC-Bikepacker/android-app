package ru.netcracker.bikepacker.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.OverlayWithIW
import ru.netcracker.bikepacker.R
import ru.netcracker.bikepacker.databinding.ActivityMainNavigationBinding
import ru.netcracker.bikepacker.tracks.GpxUtil
import ru.netcracker.bikepacker.tracks.UserTrack
import java.lang.String
import java.util.stream.Collectors
import kotlin.Boolean
import kotlin.Int
import kotlin.getValue
import kotlin.lazy
import kotlin.let
import kotlin.run
import kotlin.with


class MainNavigationActivity : AppCompatActivity() {
    companion object {
        //to save current fragment on saveInstanceState
        const val CURRENT_FRAGMENT = "current_fragment"

        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
        private val settingsFragment = SettingsFragment()
        const val INITIAL_SELECTED_ITEM: Int = R.id.navigation_home

        //tag for each fragment
        const val TAG_MAP = "map"
        const val TAG_SETTINGS = "settings"
        const val TAG_RECORD = "record"
        const val TAG_HOME = "home"
        const val TAG_FINDFRIEND = "findFriend"
        const val TAG_RECORD_SM = "record_summary"
        const val TAG_USER_MENU = "user_menu"
        const val TAG_TRACK_MENU = "track_menu"
        const val TAG_POINT = "point"
        const val TAG_OPEN = "openTrack"
        const val TAG_EDIT_ACCOUNT = "edit_account"

        var activeFragment: Fragment? = null
    }

    private var downAnim: Animation? = null
    private var upAnim: Animation? = null
    private var userTrack: UserTrack? = null
    private var binding: ActivityMainNavigationBinding? = null
    private var bitmap: Bitmap? = null

    //fragments
    private val recordSummaryFragment: RecordSummaryFragment by lazy {
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
        else MapFragment()
    }

    private val homeFragment: HomeFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_HOME)
        if (fr != null) fr as HomeFragment
        else HomeFragment()

    }

    private val findFriend: FindFriendFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_FINDFRIEND)
        if (fr != null) fr as FindFriendFragment
        else {
            FindFriendFragment()
        }
    }

    private val createPointFragment: CreatePointFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_POINT)
        if (fr != null) fr as CreatePointFragment
        else {
            val createPoint = CreatePointFragment()
            createPoint.setOnCancelButtonListener {
                findViewById<ImageButton>(R.id.locationBtn).visibility = View.VISIBLE
                findViewById<ImageButton>(R.id.zoomInBtn).visibility = View.VISIBLE
                findViewById<ImageButton>(R.id.zoomOutBtn).visibility = View.VISIBLE
                findViewById<Button>(R.id.buttonPoint).visibility = View.VISIBLE
                findViewById<EditText>(R.id.description).setText("")
                supportFragmentManager.beginTransaction()
                    .show(recordFragment)
                    .remove(createPointFragment)
                    .commit()
            }
            createPoint.setButtonPoint(findViewById<Button>(R.id.buttonPoint))
            createPoint.setRecordFragment(recordFragment)
            createPoint
        }
    }


    private val userMenuFragment: UserMenuInformationAccountFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_USER_MENU)
        if (fr != null) fr as UserMenuInformationAccountFragment
        else UserMenuInformationAccountFragment()
    }

    private val accountEditorFragment: AccountEditorFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_EDIT_ACCOUNT)
        if (fr != null) fr as AccountEditorFragment
        else AccountEditorFragment()
    }

    private val trackMenuFragment: TrackMenuFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_TRACK_MENU)
        if (fr != null) fr as TrackMenuFragment
        else TrackMenuFragment()
    }

    private val recordFragment: RecordFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_RECORD)

        if (fr != null) fr as RecordFragment
        else {
            val initialFr = RecordFragment()
            initialFr.setBtnPoint(findViewById<Button>(R.id.buttonPoint))
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
                    map.let {
                        it?.zoomToBoundingBox(userTrack?.boundingBox, true)
                        it?.overlayManager?.addAll(userTrack?.toList()!!)
                    }
                }
            }
            initialFr.setOnCreatePointListener {
                findViewById<Button>(R.id.buttonPoint).visibility = View.INVISIBLE
                findViewById<ImageButton>(R.id.locationBtn).visibility = View.INVISIBLE
                findViewById<ImageButton>(R.id.zoomInBtn).visibility = View.INVISIBLE
                findViewById<ImageButton>(R.id.zoomOutBtn).visibility = View.INVISIBLE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.create_point_container, createPointFragment, TAG_POINT)
                    .hide(recordFragment)
                    .commit()
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
            initialFr.setOnStartBtnClickListener {
                mapFragment.map.overlays.removeIf { overlay ->
                    overlay is OverlayWithIW &&
                            overlay.id == UserTrack.RECORDED_TRACK_TAG
                }
            }
            initialFr
        }
    }

    private val openTrackFragment: OpenTrackFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_OPEN)
        if (fr != null) fr as OpenTrackFragment
        else {
            val fr = OpenTrackFragment()
            fr.fragmentManager = supportFragmentManager
            fr.setOnStartRouteBtnListener {
                supportFragmentManager.beginTransaction()
                    .hide(openTrackFragment)
                    .show(mapFragment)
                    .commit()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.up_alpha_trans, R.anim.down_alpha_trans)
                    .replace(R.id.start_new_route_container, recordFragment, TAG_RECORD)
                    .commit()
                activeFragment = mapFragment

                val track = fr.getTrack()
                val map = mapFragment.map
                userTrack = UserTrack.newInstance(
                    map,
                    mapFragment.startIcon,
                    mapFragment.finishIcon,
                    GpxUtil.trackModelToPolyline(track),
                )
                mapFragment.zoomToBounds(userTrack?.boundingBox)
                mapFragment.map.overlayManager?.addAll(userTrack?.toList()!!)
            }
            fr
        }
    }

    private var ctx: Context? = null
    private var selectedFragment: Int = R.id.navigation_home

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainNavigationBinding.inflate(layoutInflater)
        ctx = applicationContext
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_compass)
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
            selectedFragment = it.getInt(CURRENT_FRAGMENT, R.id.navigation_home)
        }

        //check the selected item of bottom menu and then show corresponding fragment
        when (selectedFragment) {
            R.id.navigation_home -> activeFragment = homeFragment
            R.id.find_friends_fragment -> activeFragment = findFriend
            R.id.navigation_account -> activeFragment = userMenuFragment
            R.id.navigation_tracks -> activeFragment = trackMenuFragment
            //TODO: R.id.navigation_{required menu button} -> activeFragment = {required fragment}
        }

        if (savedInstanceState == null) {
            //add all fragments but show only
            //active fragment
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, mapFragment, TAG_MAP).hide(mapFragment)
                .add(R.id.fragment_container, homeFragment, TAG_HOME).hide(homeFragment)
                .add(R.id.fragment_container, findFriend, TAG_FINDFRIEND).hide(findFriend)
                .add(R.id.fragment_container, userMenuFragment, TAG_USER_MENU).hide(userMenuFragment)
                .add(R.id.fragment_container, trackMenuFragment, TAG_TRACK_MENU).hide(trackMenuFragment)
                .add(R.id.fragment_container, accountEditorFragment, TAG_EDIT_ACCOUNT).hide(accountEditorFragment)
                .add(R.id.fragment_container, openTrackFragment, TAG_OPEN).hide(openTrackFragment)
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

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_compass)
        Log.d("!!!!!!!!!!", String.valueOf(bitmap))

    }

    private fun setFragment(itemId: Int): Boolean {
        selectedFragment = itemId
        if (activeFragment is MapFragment) {
            mapFragment.map.let {
                while (it.overlayManager.size > 2) {
                    it.overlayManager.removeLast()
                }
            }
        }
        if (activeFragment is TrackMenuFragment) {
            supportFragmentManager.beginTransaction().hide(openTrackFragment)
                .commit()
        }
        when (itemId) {
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

            R.id.navigation_account -> {
                if (activeFragment !is UserMenuInformationAccountFragment) {

                    val editButton: Button =
                        findViewById(R.id.editButtonInformationAccountUserMenuFragment)
                    editButton.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            supportFragmentManager
                                .beginTransaction()
                                .hide(activeFragment!!)
                                .show(accountEditorFragment).commit()

                            activeFragment = accountEditorFragment
                        }
                    })

                    supportFragmentManager.beginTransaction().hide(activeFragment!!)
                        .show(userMenuFragment)
                        .commit()
                }
                activeFragment = userMenuFragment
            }

            R.id.navigation_tracks -> {
                if (activeFragment !is TrackMenuFragment) {
                    supportFragmentManager.beginTransaction().hide(activeFragment!!)
                        .show(trackMenuFragment)
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction().hide(openTrackFragment).show(trackMenuFragment).commit()
                }
                activeFragment = trackMenuFragment
            }

            R.id.navigation_home -> {
                if (activeFragment is HomeFragment) return false
                supportFragmentManager
                    .beginTransaction()
                    .hide(activeFragment!!)
                    .show(homeFragment).commit()

                activeFragment = homeFragment
            }

            R.id.navigation_friends -> {
                findFriend.disp()
                if (activeFragment !is FindFriendFragment) {
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment!!)
                        .show(findFriend)
                        .commit()
                }
                activeFragment = findFriend
            }

            else -> {
                if (activeFragment is UserMenuInformationAccountFragment) return false
                supportFragmentManager.beginTransaction().hide(activeFragment!!)
                    .show(userMenuFragment).commit()

                activeFragment = userMenuFragment
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
