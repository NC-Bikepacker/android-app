package ru.netcracker.bikepacker.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Optional;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.homemenu.HomeMenuPagerAdapter;

public class HomeFragment extends Fragment {
    private View viewHomeMenu;
    private ViewPager2 viewPager;
    private TabLayout tabLayoutHomeMenu;
    private TabLayout.OnTabSelectedListener listener;
    private ImageButton addNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewHomeMenu = inflater.inflate(R.layout.fragment_home, container,false);

        viewPager = viewHomeMenu.findViewById(R.id.homeMenuViewPager);
        viewPager.setAdapter(new HomeMenuPagerAdapter(this,this.getContext()));
        tabLayoutHomeMenu = viewHomeMenu.findViewById(R.id.homeMenuTabLayout);

        this.listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getIcon()!=null){
                    tab.getIcon().setTint(viewHomeMenu.getContext().getColor(R.color.white));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getIcon()!=null) {
                    tab.getIcon().setTint(viewHomeMenu.getContext().getColor(R.color.tabIconColor));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayoutHomeMenu, viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setIcon(R.drawable.ic_newspaper_folded__1_);
                    if(tab.getIcon()!=null) {
                        tab.getIcon().setTint(viewHomeMenu.getContext().getColor(R.color.tabIconColor));
                    }
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_track_inspection);
                    if(tab.getIcon()!=null) {
                        tab.getIcon().setTint(viewHomeMenu.getContext().getColor(R.color.tabIconColor));
                    }
                    break;
            }
        });
        tabLayoutHomeMenu.addOnTabSelectedListener(listener);
        tabLayoutMediator.attach();
        return viewHomeMenu;
    }

    private void addNews(){
        Optional<Fragment> activeFragment = Optional.ofNullable(MainNavigationActivity.Companion.getActiveFragment());
        CreateNewsCard createNewsCard = new CreateNewsCard();
        if(activeFragment.isPresent()){
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, createNewsCard, "TAG_CREATE_NEWS_CARD")
                    .hide(activeFragment.get())
                    .show(createNewsCard)
                    .commit();
            MainNavigationActivity.Companion.setActiveFragment(createNewsCard);
        }
    }
}