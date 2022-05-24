package ru.netcracker.bikepacker.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.trackmenu.TrackMenuPagerAdapter;
import ru.netcracker.bikepacker.service.GpxFileManager;


public class TrackMenuFragment extends Fragment {
    private ViewPager2 viewPager;
    private View viewTrackMenu;
    private TabLayout tabLayoutUserMenu;
    private TabLayout.OnTabSelectedListener listener;
    private FloatingActionButton importGpxButton;
    private final GpxFileManager gpxFileManager = new GpxFileManager(getContext());
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewTrackMenu = inflater.inflate(R.layout.fragment_track_menu, container, false);

        importGpxButton = viewTrackMenu.findViewById(R.id.importTrackButton);
        importGpxButton.setOnClickListener(view -> gpxFileManager.importGpx(getContext()));

        viewPager = viewTrackMenu.findViewById(R.id.trackMenuViewPager);
        viewPager.setAdapter(new TrackMenuPagerAdapter(this,this.getContext()));

        tabLayoutUserMenu = viewTrackMenu.findViewById(R.id.trackMenuTableLayout);

        this.listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setTint(viewTrackMenu.getContext().getColor(R.color.white));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setTint(viewTrackMenu.getContext().getColor(R.color.tabIconColor));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayoutUserMenu, viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setIcon(R.drawable.ic_desk_fill);
                    Objects.requireNonNull(tab.getIcon()).setTint(viewTrackMenu.getContext().getColor(R.color.tabIconColor));
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_star_small);
                    Objects.requireNonNull(tab.getIcon()).setTint(viewTrackMenu.getContext().getColor(R.color.tabIconColor));
                    break;
            }
        });
        tabLayoutUserMenu.addOnTabSelectedListener(listener);
        tabLayoutMediator.attach();
        return viewTrackMenu;
    }


}