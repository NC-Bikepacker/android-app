package ru.netcracker.bikepacker.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.TracksRecyclerAdapter;
import ru.netcracker.bikepacker.adapter.homemenu.HomeMenuRecyclerAdapter;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.NewsCardModel;
import ru.netcracker.bikepacker.model.TrackModel;

public class HomeMenuRecycleViewFragment extends Fragment {

    private View view;
    private int position;
    private RecyclerView homeMenuRecyclerView;
    private HomeMenuRecyclerAdapter homeMenuRecyclerAdapter;
    private TracksRecyclerAdapter tracksRecyclerAdapter;

    private RetrofitManager retrofitManager;
    private UserAccountManager userAccountManager;

    public HomeMenuRecycleViewFragment(int position) {
        super(position);
        this.position = position;
        this.retrofitManager = RetrofitManager.getInstance(this.getContext());
        this.userAccountManager = UserAccountManager.getInstance(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_menu_recycle_view, container, false);

        switch (position){
            //all used tracks
            case 0:
                setHomeMenuRecyclerFragment(view);
                getNews();
                break;
            // favorite tracks
            case 1:
                setHomeMenuRecyclerFragment(view);
                getFriendTracks();
                break;
        }
        return view;
    }

    private void setHomeMenuRecyclerFragment(View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        homeMenuRecyclerView = view.findViewById(R.id.recycleViewHomeFragment);
        homeMenuRecyclerView.setLayoutManager(layoutManager);
    }

    private void getNews(){
        List<NewsCardModel> news = new ArrayList<>();

        retrofitManager.getJSONApi()
                .getNews(userAccountManager.getCookie(),userAccountManager.getUser().getId())
                .enqueue(new Callback<List<NewsCardModel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<NewsCardModel>> call, @NonNull Response<List<NewsCardModel>> response) {
                        if(response.isSuccessful()){
                            news.clear();
                            news.addAll(Objects.requireNonNull(response.body()));

                            homeMenuRecyclerAdapter = new HomeMenuRecyclerAdapter(getContext(), news);
                            homeMenuRecyclerView.setAdapter(homeMenuRecyclerAdapter);
                        }
                        else {
                            Toast.makeText(getContext(), "Check the internet connection", Toast.LENGTH_SHORT).show();
                            Log.e("Error news in homeMenu fragment", "Error response successful response. Error message: "+ response.message() + ". Error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<NewsCardModel>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "News output error. Check internet connection", Toast.LENGTH_SHORT).show();
                        Log.e("RetrofitError", "Error in HomeMenuRecycleViewFragment.class. Error: "+ t.getMessage() , t);
                    }
                });
    }

    private void getFriendTracks(){
        List<TrackModel> tracks = new ArrayList<>();

        retrofitManager.getJSONApi()
                .getLastFriendTrack(userAccountManager.getCookie(), userAccountManager.getUser().getId())
                .enqueue(new Callback<List<TrackModel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<TrackModel>> call, @NonNull Response<List<TrackModel>> response) {
                        if(response.isSuccessful()){
                            tracks.clear();
                            assert response.body() != null;
                            tracks.addAll(response.body());

                            tracksRecyclerAdapter = new TracksRecyclerAdapter(getContext(), tracks);
                            homeMenuRecyclerView.setAdapter(tracksRecyclerAdapter);
                        }
                        else {
                            Toast.makeText(getContext(), "Check the internet connection", Toast.LENGTH_SHORT).show();
                            Log.e("Error show last tracks friends in homeMenu fragment", "Error response successful response. Error message: "+ response.message() + ". Error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<TrackModel>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Track output error. Check internet connection", Toast.LENGTH_SHORT).show();
                        Log.e("Error show last tracks friends in homeMenu fragment", "Error friend track response. Error message: "+ t.getMessage(), t);
                    }
                });
    }

}