package ru.netcracker.bikepacker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class HomeFragment extends Fragment {

    private ImageButton findFriends;
    private Fragment fragment;
    private FragmentManager fragmentManager;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container,false);
        findFriends = view.findViewById(R.id.findFriendsButton);

        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new FindFriendFragment();
                fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager
                        .beginTransaction()
                        .replace(R.id.center_fragment_container, fragment)
                        .commit();
            }
        });
        return view;
    }
}