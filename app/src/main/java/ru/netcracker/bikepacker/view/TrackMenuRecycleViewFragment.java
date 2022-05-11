package ru.netcracker.bikepacker.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.TracksRecyclerAdapter;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.TrackModel;


public class TrackMenuRecycleViewFragment extends Fragment {

    private View view;
    private int position;
    private RecyclerView userMenuRecyclerView;
    private TracksRecyclerAdapter recyclerAdapter;
    private RetrofitManager retrofitManager;
    private UserAccountManager userAccountManager;

    public TrackMenuRecycleViewFragment(int position) {
        this.position = position;
        this.retrofitManager = RetrofitManager.getInstance(this.getContext());
        this.userAccountManager = UserAccountManager.getInstance(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_track_menu_recycle_view, container, false);
        setTrackMenuRecyclerFragment(view);

        switch (position) {
            //all used tracks
            case 0:
                getAllUsedTracks();
                break;
            // favorite tracks
            case 1:
                getFavoriteTracks();
                break;
        }

        return view;
    }

    private void setTrackMenuRecyclerFragment(View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        userMenuRecyclerView = view.findViewById(R.id.userMenuRecyclerView);
        userMenuRecyclerView.setLayoutManager(layoutManager);
    }

    private List<TrackModel> getFavoriteTracks(){
        List<TrackModel> tracks = new ArrayList<>();
           retrofitManager.getJSONApi()
                        .getMyFavoriteTracks(userAccountManager.getCookie(), userAccountManager.getUser().getId())
                        .enqueue(new Callback<List<TrackModel>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<TrackModel>> call, @NonNull Response<List<TrackModel>> response) {
                                if(response.body()!=null){
                                    tracks.addAll(response.body());
                                }
                                recyclerAdapter = new TracksRecyclerAdapter(getContext(), tracks);
                                userMenuRecyclerView.setAdapter(recyclerAdapter);
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<TrackModel>> call, @NonNull Throwable t) {
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
                    public void onResponse(@NonNull Call<List<TrackModel>> call, @NonNull Response<List<TrackModel>> response) {
                        if(response.body()!=null){
                            tracks.addAll(response.body());
                        }
                        recyclerAdapter = new TracksRecyclerAdapter(getContext(), tracks);
                        userMenuRecyclerView.setAdapter(recyclerAdapter);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<TrackModel>> call, @NonNull Throwable t) {
                        Log.e("UserMenuRecycleViewFragment error","Error get user tracks: " + t.getMessage(),t);
                    }
                });
        return tracks;
    }
}