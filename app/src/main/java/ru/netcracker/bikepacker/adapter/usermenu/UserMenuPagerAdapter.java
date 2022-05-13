package ru.netcracker.bikepacker.adapter.usermenu;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ru.netcracker.bikepacker.view.UserMenuInformationAccountFragment;
import ru.netcracker.bikepacker.view.UserMenuRecycleViewFragment;

public class UserMenuPagerAdapter extends FragmentStateAdapter {
    private Context ctx;
    public UserMenuPagerAdapter(@NonNull Fragment fragment, Context ctx) {
        super(fragment);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            //all used tracks
            case 0:
                return new UserMenuRecycleViewFragment(position);
            //favorite tracks
            case 1:
                return new UserMenuRecycleViewFragment(position);
            //user information
            default:
                return new UserMenuInformationAccountFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
