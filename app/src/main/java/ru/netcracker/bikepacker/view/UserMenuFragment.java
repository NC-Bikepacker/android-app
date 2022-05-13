package ru.netcracker.bikepacker.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.osmdroid.util.GeoPoint;

import java.util.Optional;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.usermenu.UserMenuPagerAdapter;
import ru.netcracker.bikepacker.manager.UserAccountManager;

public class UserMenuFragment extends Fragment {
    private ViewPager2 viewPager;
    private View viewUserMenu;
    private TabLayout tabLayoutUserMenu;
    private TabLayout.OnTabSelectedListener listener;
    private UserAccountManager userAccountManager;
    private LocationManager locationManager;
    private Context ctx;

    public Optional<GeoPoint> getLastLocation() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Optional.empty();
        }
        Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (userLocation == null) {
            return Optional.empty();
        }
        return Optional.of(new GeoPoint(userLocation));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewUserMenu = inflater.inflate(R.layout.fragment_user_menu, container, false);
        ctx = requireContext();
        userAccountManager = UserAccountManager.getInstance(ctx);
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        viewPager = viewUserMenu.findViewById(R.id.userMenuViewPager);
        Spinner spinner = viewUserMenu.findViewById(R.id.spinner);
        viewPager.setAdapter(new UserMenuPagerAdapter(this,this.getContext()));

        tabLayoutUserMenu = viewUserMenu.findViewById(R.id.userMenuTableLayout);

        this.listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setTint(viewUserMenu.getContext().getColor(R.color.white));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setTint(viewUserMenu.getContext().getColor(R.color.tabIconColor));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctx,
                R.array.filter_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayoutUserMenu, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.ic_desk_fill);
                        tab.getIcon().setTint(viewUserMenu.getContext().getColor(R.color.tabIconColor));
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_star_small);
                        tab.getIcon().setTint(viewUserMenu.getContext().getColor(R.color.tabIconColor));
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_user_fill);
                        tab.getIcon().setTint(viewUserMenu.getContext().getColor(R.color.tabIconColor));
                        break;
                }
            }
        });
        tabLayoutUserMenu.addOnTabSelectedListener(listener);

        tabLayoutMediator.attach();

        return viewUserMenu;
    }
}