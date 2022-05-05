package ru.netcracker.bikepacker.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.homemenu.HomeMenuPagerAdapter;
import ru.netcracker.bikepacker.adapter.trackmenu.TrackMenuPagerAdapter;

public class HomeFragment extends Fragment {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private ImageButton findFriends;
    private ViewPager2 viewPager;
    private View viewHomeMenu;
    private TabLayout tabLayoutHomeMenu;
    private TabLayout.OnTabSelectedListener listener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewHomeMenu = inflater.inflate(R.layout.fragment_home, container,false);
        findFriends = viewHomeMenu.findViewById(R.id.findFriendsButton);

        viewPager = viewHomeMenu.findViewById(R.id.homeMenuViewPager);
        viewPager.setAdapter(new HomeMenuPagerAdapter(this,this.getContext()));
        tabLayoutHomeMenu = viewHomeMenu.findViewById(R.id.homeMenuTabLayout);

        this.listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setTint(viewHomeMenu.getContext().getColor(R.color.white));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setTint(viewHomeMenu.getContext().getColor(R.color.tabIconColor));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayoutHomeMenu, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setIcon(R.drawable.ic_newspaper_folded__1_);
                        tab.getIcon().setTint(viewHomeMenu.getContext().getColor(R.color.tabIconColor));
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_track_inspection);
                        tab.getIcon().setTint(viewHomeMenu.getContext().getColor(R.color.tabIconColor));
                        break;
                }
            }
        });
        tabLayoutHomeMenu.addOnTabSelectedListener(listener);
        tabLayoutMediator.attach();
        return viewHomeMenu;
    }
}