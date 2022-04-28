package ru.netcracker.bikepacker.adapter.usermenu;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ru.netcracker.bikepacker.view.UserMenuInformationAccountFragment;
import ru.netcracker.bikepacker.view.UserMenuRecycleViewFragment;

public class UserMenuPagerAdapter extends FragmentStateAdapter {

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private FragmentManager fragmentManager;

    public UserMenuPagerAdapter(@NonNull Fragment fragment, Context context) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            //all used tracks
            case 0:
                UserMenuRecycleViewFragment userMenuRecycleViewFragment = new UserMenuRecycleViewFragment(position);
                userMenuRecycleViewFragment.setFragmentManager(fragmentManager);
                return userMenuRecycleViewFragment;
            //favorite tracks
            case 1:
                UserMenuRecycleViewFragment userMenuRecycleViewFragment1 = new UserMenuRecycleViewFragment(position);
                userMenuRecycleViewFragment1.setFragmentManager(fragmentManager);
                return userMenuRecycleViewFragment1;
            //user information
            case 2:
                return new UserMenuInformationAccountFragment();
            default:
                return new UserMenuInformationAccountFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
