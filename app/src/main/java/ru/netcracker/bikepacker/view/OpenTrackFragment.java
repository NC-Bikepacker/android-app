package ru.netcracker.bikepacker.view;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.TracksRecyclerAdapter;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.ImageModel;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.service.ImageConverter;
import ru.netcracker.bikepacker.tracks.listeners.OnStartRouteBtnListener;

public class OpenTrackFragment extends Fragment {
    private Button buttonStartRoute;
    private RetrofitManager retrofitManager;
    private UserAccountManager userAccountManager;
    private TrackModel track;

    ImageConverter imageConverter = new ImageConverter();

    ImageView userPicItemUserMenu;
    ImageButton addFavoriteTrackButton,
            addImportantTrack,
            chatAltFillButton,
            shareTrackButton,
            exportGpxButton;
    TextView firstAndLastnameUserMenuItem,
            dateUserMenuItem,
            distanceTextViewUserMenu,
            avgSpeedTextViewUserMenu,
            timeTextViewUserMenu;
    ImageView itemUserMenuMapImage;
    private FragmentManager fragmentManager;

    public void setTrack(TrackModel track) {
        this.track = track;
    }

    private String getConvertTravelTime(TrackModel track) {
        /*Convert travel time in seconds to readable string in format HH:MM:SS*/
        long travelTime = track.getTravelTime();
        long sec = travelTime % 60;
        long min = (travelTime / 60) % 60;
        long hours = (travelTime / 60) / 60;

        String trTimeSeconds = (sec < 10) ? "0" + sec : Long.toString(sec);
        String trTimeMinutes = (min < 10) ? "0" + min : Long.toString(min);
        String trTimeHours = (hours < 10) ? "0" + hours : Long.toString(hours);

        return trTimeHours + ":" + trTimeMinutes + ":" + trTimeSeconds;
    }

    public void openTrack() {
        this.retrofitManager = RetrofitManager.getInstance(getContext());
        this.userAccountManager = UserAccountManager.getInstance(getContext());
        Picasso.get()
                .load(track.getUser().getUserPicLink())
                .placeholder(R.drawable.ic_userpic)
                .error(R.drawable.ic_userpic)
                .into(userPicItemUserMenu);
        firstAndLastnameUserMenuItem.setText(String.format("%s %s", track.getUser().getFirstname(), track.getUser().getLastname()));
        timeTextViewUserMenu.setText(getConvertTravelTime(track));
        retrofitManager.getJSONApi()
                .getTrackImage(userAccountManager.getCookie(), track.getTrackId())
                .enqueue(new Callback<ImageModel>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(@NonNull Call<ImageModel> call, @NonNull Response<ImageModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            itemUserMenuMapImage.setImageBitmap(imageConverter.decode(response.body().getImageBase64()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ImageModel> call, @NonNull Throwable t) {
                        Log.e(TracksRecyclerAdapter.class.getName(), "Error responce image model: " + t.getMessage(), t);
                    }
                });
    }

    public TrackModel getTrack() {
        return track;
    }
    OnStartRouteBtnListener onStartRouteBtnListener;

    public void setOnStartRouteBtnListener(OnStartRouteBtnListener onStartRouteBtnListener) {
        this.onStartRouteBtnListener = onStartRouteBtnListener;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onViewCreated(@NonNull View itemView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(itemView, savedInstanceState);
        buttonStartRoute = itemView.findViewById(R.id.startRouteButton);
        buttonStartRoute.setOnClickListener(view1 -> onStartRouteBtnListener.onClick());
        this.userPicItemUserMenu = itemView.findViewById(R.id.userPicItemUserMenu);
        this.addFavoriteTrackButton = itemView.findViewById(R.id.addFavoriteTrackButton);
        this.addImportantTrack = itemView.findViewById(R.id.addImportantTrack);
        this.chatAltFillButton = itemView.findViewById(R.id.chatAltFillButton);
        this.shareTrackButton = itemView.findViewById(R.id.shareTrackButton);
        this.exportGpxButton = itemView.findViewById(R.id.exportTrackButton);
        this.firstAndLastnameUserMenuItem = itemView.findViewById(R.id.firstAndLastnameUserMenuItem);
        this.dateUserMenuItem = itemView.findViewById(R.id.dateUserMenuItem);
        this.distanceTextViewUserMenu = itemView.findViewById(R.id.distanceTextViewUserMenu);
        this.avgSpeedTextViewUserMenu = itemView.findViewById(R.id.avgSpeedTextViewUserMenu);
        this.timeTextViewUserMenu = itemView.findViewById(R.id.timeTextViewUserMenu);
        this.itemUserMenuMapImage = itemView.findViewById(R.id.itemUserMenuMapImage);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_open_track, container, false);
    }
}
