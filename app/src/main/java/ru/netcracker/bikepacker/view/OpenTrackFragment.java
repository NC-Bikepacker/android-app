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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.osmdroid.views.overlay.Polyline;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import io.jenetics.jpx.GPX;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.usermenu.UserMenuRecyclerAdapter;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.ImageModel;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.service.ImageConverter;
import ru.netcracker.bikepacker.tracks.GpxUtil;
import ru.netcracker.bikepacker.tracks.listeners.OnGpxCreatedListener;
import ru.netcracker.bikepacker.tracks.listeners.OnStartRouteBtnListener;

public class OpenTrackFragment extends Fragment {
    private Button buttonStartRoute;
    private RetrofitManager retrofitManager;
    private UserAccountManager userAccountManager;

    ImageConverter imageConverter = new ImageConverter();

    ImageView userPicItemUserMenu;
    ImageButton addFavoriteTrackButton,
            addImportantTrack,
            chatAltFillButton,
            uploadTrackButton;
    TextView firstAndLastnameUserMenuItem,
            dateUserMenuItem,
            trackNameTextView,
            distanceTextViewUserMenu,
            avgSpeedTextViewUserMenu,
            timeTextViewUserMenu;
    ImageView itemUserMenuMapImage;
    private FragmentManager fragmentManager;

    public void setTrack(TrackModel track) {
        this.track = track;
    }

    public void openTrack(){
        this.retrofitManager = RetrofitManager.getInstance(getContext());
        this.userAccountManager = UserAccountManager.getInstance(getContext());
        Picasso.get()
                .load(track.getUser().getUserPicLink())
                .placeholder(R.drawable.ic_userpic)
                .error(R.drawable.ic_userpic)
                .into(userPicItemUserMenu);
        firstAndLastnameUserMenuItem.setText(track.getUser().getFirstname() +
                " " +
                track.getUser().getLastname());

        /*Convert travel time in seconds to readable string in format HH:MM:SS*/
        long travelTime = track.getTravelTime();
        long sec = travelTime % 60;
        long min = (travelTime / 60) % 60;
        long hours = (travelTime / 60) / 60;

        String trTimeSecons = (sec < 10) ? "0" + Long.toString(sec) : Long.toString(sec);
        String trTimeMinutes = (min < 10) ? "0" + Long.toString(min) : Long.toString(min);
        String trTimeHours = (hours < 10) ? "0" + Long.toString(hours) : Long.toString(hours);

        String travelTimeString = trTimeHours + ":" + trTimeMinutes + ":" + trTimeSecons;


        timeTextViewUserMenu.setText(String.valueOf(travelTimeString));

        retrofitManager.getJSONApi()
                .getTrackImage(userAccountManager.getCookie(), track.getTrackId())
                .enqueue(new Callback<ImageModel>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            itemUserMenuMapImage.setImageBitmap(imageConverter.decode(response.body().getImageBase64()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageModel> call, Throwable t) {
                        Log.e(UserMenuRecyclerAdapter.class.getName(), "Error responce image model: " + t.getMessage(), t);
                    }
                });

    }

    public TrackModel getTrack() {
        return track;
    }
//    public Polyline getPolyline(){
//        GPX gpx = GPX.builder().build();
//        gpx.tracks().collect(Collectors.toList()).get(0);
//        return track.getGpx();
//    }

    TrackModel track;
    OnStartRouteBtnListener onStartRouteBtnListener;

    public void setOnStartRouteBtnListener(OnStartRouteBtnListener onStartRouteBtnListener) {
        this.onStartRouteBtnListener = onStartRouteBtnListener;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonStartRoute = view.findViewById(R.id.startRouteButton);
        buttonStartRoute.setOnClickListener(view1 -> onStartRouteBtnListener.onClick());
        this.userPicItemUserMenu = view.findViewById(R.id.userPicItemUserMenu);
        this.addFavoriteTrackButton = view.findViewById(R.id.addFavoriteTrackButton);
        this.addImportantTrack = view.findViewById(R.id.addImportantTrack);
        this.chatAltFillButton = view.findViewById(R.id.chatAltFillButton);
        this.uploadTrackButton = view.findViewById(R.id.shareTrackButton);
        this.firstAndLastnameUserMenuItem = view.findViewById(R.id.firstAndLastnameUserMenuItem);
        this.dateUserMenuItem = view.findViewById(R.id.dateUserMenuItem);
        this.distanceTextViewUserMenu = view.findViewById(R.id.distanceTextViewUserMenu);
        this.avgSpeedTextViewUserMenu = view.findViewById(R.id.avgSpeedTextViewUserMenu);
        this.timeTextViewUserMenu = view.findViewById(R.id.timeTextViewUserMenu);
        this.itemUserMenuMapImage = view.findViewById(R.id.itemUserMenuMapImage);


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
