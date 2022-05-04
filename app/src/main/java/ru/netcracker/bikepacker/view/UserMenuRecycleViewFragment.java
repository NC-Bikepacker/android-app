package ru.netcracker.bikepacker.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.usermenu.UserMenuRecyclerAdapter;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.service.GpxFileManager;


public class UserMenuRecycleViewFragment extends Fragment {

    private View view;
    private final int position;
    private RecyclerView userMenuRecyclerView;
    private UserMenuRecyclerAdapter recyclerAdapter;
    private final RetrofitManager retrofitManager;
    private final UserAccountManager userAccountManager;
    private final GpxFileManager gpxFileManager;

    private FloatingActionButton importGpxButton;

    public UserMenuRecycleViewFragment(int position) {
        this.position = position;
        this.retrofitManager = RetrofitManager.getInstance(this.getContext());
        this.userAccountManager = UserAccountManager.getInstance(this.getContext());
        this.gpxFileManager = new GpxFileManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_menu_recycle_view, container, false);

        importGpxButton = view.findViewById(R.id.importTrackButton);

        switch (position) {
            //all used tracks
            case 0:
                setUserMenuRecyclerFragment(getAllUsedTracks(), view);
                break;
            // favorite tracks
            case 1:
                setUserMenuRecyclerFragment(getFavoriteTracks(), view);
                break;
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        importGpxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpxFileManager.importGpx(getContext());
            }
        });
    }

    private class DownloadTracks extends AsyncTask<View, Void, TrackModel> {

        @Override
        protected TrackModel doInBackground(View... views) {
            return null;
        }
    }

    private void setUserMenuRecyclerFragment(List<TrackModel> tracks, View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        userMenuRecyclerView = view.findViewById(R.id.userMenuRecyclerView);
        userMenuRecyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new UserMenuRecyclerAdapter(getContext(), tracks);
        userMenuRecyclerView.setAdapter(recyclerAdapter);
    }

    private List<TrackModel> getFavoriteTracks(){
        List<TrackModel> tracks = new ArrayList<>();
           retrofitManager.getJSONApi()
                        .getMyFavoriteTracks(userAccountManager.getCookie(), userAccountManager.getUser().getId())
                        .enqueue(new Callback<List<TrackModel>>() {
                            @Override
                            public void onResponse(Call<List<TrackModel>> call, Response<List<TrackModel>> response) {
                                if(response.body()!=null){
                                    tracks.addAll(response.body());
                                }

                                recyclerAdapter = new UserMenuRecyclerAdapter(getContext(), tracks);
                                userMenuRecyclerView.setAdapter(recyclerAdapter);
                            }

                            @Override
                            public void onFailure(Call<List<TrackModel>> call, Throwable t) {
                                Log.e("UserMenuRecycleViewFragment error","Error get favorite tracks: " + t.getMessage(),t);
                            }
                        });
           return tracks;
    }


    private List<TrackModel> getAllUsedTracks(){
        List<TrackModel> tracks = new ArrayList<>();
        retrofitManager.getJSONApi()
                .getTracksByUser(userAccountManager.getCookie(), userAccountManager.getUser().getId())
                .enqueue(new Callback<List<TrackModel>>() {
                    @Override
                    public void onResponse(Call<List<TrackModel>> call, Response<List<TrackModel>> response) {
                        if(response.body()!=null){
                            tracks.addAll(response.body());
                        }

                        recyclerAdapter = new UserMenuRecyclerAdapter(getContext(), tracks);
                        userMenuRecyclerView.setAdapter(recyclerAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<TrackModel>> call, Throwable t) {
                        Log.e("UserMenuRecycleViewFragment error","Error get user tracks: " + t.getMessage(),t);
                    }
                });
        return tracks;
    }
}