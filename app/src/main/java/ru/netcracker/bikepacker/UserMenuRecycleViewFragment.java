package ru.netcracker.bikepacker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.netcracker.bikepacker.adapter.FindFriendAdapter;
import ru.netcracker.bikepacker.adapter.usermenu.UserMenuRecyclerAdapter;
import ru.netcracker.bikepacker.model.TrackDTO;
import ru.netcracker.bikepacker.model.User;


public class UserMenuRecycleViewFragment extends Fragment {

    View view;
    int position;
    RecyclerView userMenuRecyclerView;
    UserMenuRecyclerAdapter recyclerAdapter;

    public UserMenuRecycleViewFragment(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_menu_recycle_view, container, false);

        switch (position){
            //all used tracks
            case 0:
                setUserMenuRecyclerFragment(getAllUsedTracks(),view);
                break;
            // favorite tracks
            case 1:
                setUserMenuRecyclerFragment(getFavoriteTracks(),view);
                break;
        }

        return view;

    }

    private void setUserMenuRecyclerFragment(List<TrackDTO> tracks, View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        userMenuRecyclerView = view.findViewById(R.id.userMenuRecyclerView);
        userMenuRecyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new UserMenuRecyclerAdapter(getContext(), tracks);
        userMenuRecyclerView.setAdapter(recyclerAdapter);
    }

    private List<TrackDTO> getFavoriteTracks(){
        List<TrackDTO> tracks = new ArrayList<>();
        User iAmUser = new User(5L,"favorite","tracks","ComeBak",null);
        TrackDTO track = new TrackDTO(899564654, 3,iAmUser,"gpx GPX gpx GPX");
        tracks.add(track);
        return tracks;
    }

    private List<TrackDTO> getAllUsedTracks(){
        List<TrackDTO> tracks = new ArrayList<>();
        User iAmUser = new User(5L,"all used","tracks","ComeBak",null);
        TrackDTO track = new TrackDTO(899564654, 3,iAmUser,"gpx GPX gpx GPX");
        TrackDTO track2 = new TrackDTO(12333123, 8,iAmUser,"gpx GPX gpx GPX");
        tracks.add(track);
        tracks.add(track2);
        return tracks;
    }
}