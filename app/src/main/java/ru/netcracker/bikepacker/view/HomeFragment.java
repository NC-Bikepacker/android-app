package ru.netcracker.bikepacker.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ru.netcracker.bikepacker.R;

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
        return view;
    }
}