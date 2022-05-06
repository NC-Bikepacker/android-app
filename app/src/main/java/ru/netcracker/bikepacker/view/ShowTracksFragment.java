package ru.netcracker.bikepacker.view;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.TracksRecyclerAdapter;
import ru.netcracker.bikepacker.model.TrackModel;

public class ShowTracksFragment extends Fragment {

    private View showTracksView;
    private RecyclerView showTracksRecyclerView;
    private TracksRecyclerAdapter recyclerAdapter;
    List<TrackModel> trackModelList;

    public ShowTracksFragment(List<TrackModel> trackModelList) {
        this.trackModelList = trackModelList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        showTracksView = inflater.inflate(R.layout.fragment_show_tracks, container, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        showTracksRecyclerView = showTracksView.findViewById(R.id.showTrackFragmentRecycleView);
        showTracksRecyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new TracksRecyclerAdapter(getContext(), trackModelList);
        showTracksRecyclerView.setAdapter(recyclerAdapter);

        return showTracksView;
    }
}