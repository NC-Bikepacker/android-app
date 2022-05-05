package ru.netcracker.bikepacker.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.trackmenu.TrackMenuPagerAdapter;


public class TrackMenuFragment extends Fragment {
    private ViewPager2 viewPager;
    private View viewUserMenu;
    private TabLayout tabLayoutUserMenu;
    private TabLayout.OnTabSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewUserMenu = inflater.inflate(R.layout.fragment_track_menu, container, false);

        viewPager = viewUserMenu.findViewById(R.id.trackMenuViewPager);
        viewPager.setAdapter(new TrackMenuPagerAdapter(this,this.getContext()));
        tabLayoutUserMenu = viewUserMenu.findViewById(R.id.trackMenuTableLayout);

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

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayoutUserMenu, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setIcon(R.drawable.ic_desk_fill);
                        tab.getIcon().setTint(viewUserMenu.getContext().getColor(R.color.tabIconColor));
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_star_small);
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