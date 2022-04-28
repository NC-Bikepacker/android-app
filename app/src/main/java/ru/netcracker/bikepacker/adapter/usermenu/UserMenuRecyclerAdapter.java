package ru.netcracker.bikepacker.adapter.usermenu;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.ImageModel;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.service.ImageConverter;

public class UserMenuRecyclerAdapter extends RecyclerView.Adapter<UserMenuRecyclerAdapter.UserMenuRecyclerViewHolder> {

    private Context context;
    private List<TrackModel> tracks;
    private RetrofitManager retrofitManager;
    private UserAccountManager userAccountManager;
    private ImageConverter imageConverter;
    FragmentManager fragmentManager;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    public UserMenuRecyclerAdapter(Context context, List<TrackModel> tracks) {
        this.context = context;
        this.tracks = tracks;
        this.retrofitManager = RetrofitManager.getInstance(context);
        this.userAccountManager = UserAccountManager.getInstance(context);
        this.imageConverter = new ImageConverter();
    }

    @NonNull
    @Override
    public UserMenuRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tracksItem = LayoutInflater.from(context).inflate(R.layout.item_track_user_menu, parent, false);
        return new UserMenuRecyclerAdapter.UserMenuRecyclerViewHolder(tracksItem);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMenuRecyclerViewHolder holder, int position) {
        TrackModel track = tracks.get(position);

        Picasso.get()
                .load(track.getUser().getUserPicLink())
                .placeholder(R.drawable.ic_userpic)
                .error(R.drawable.ic_userpic)
                .into(holder.userPicItemUserMenu);

        holder.setFragmentManager(fragmentManager);
        holder.firstAndLastnameUserMenuItem.setText(tracks.get(position).getUser().getFirstname() +
                " " +
                tracks.get(position).getUser().getLastname());

        /*Convert travel time in seconds to readable string in format HH:MM:SS*/
        long travelTime = tracks.get(position).getTravelTime();
        long sec = travelTime % 60;
        long min = (travelTime / 60) % 60;
        long hours = (travelTime / 60) / 60;

        String trTimeSecons = (sec < 10) ? "0" + Long.toString(sec) : Long.toString(sec);
        String trTimeMinutes = (min < 10) ? "0" + Long.toString(min) : Long.toString(min);
        String trTimeHours = (hours < 10) ? "0" + Long.toString(hours) : Long.toString(hours);

        String travelTimeString = trTimeHours + ":" + trTimeMinutes + ":" + trTimeSecons;


        holder.timeTextViewUserMenu.setText(String.valueOf(travelTimeString));

        retrofitManager.getJSONApi()
                .getTrackImage(userAccountManager.getCookie(), tracks.get(position).getTrackId())
                .enqueue(new Callback<ImageModel>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            holder.itemUserMenuMapImage.setImageBitmap(imageConverter.decode(response.body().getImageBase64()));
//                                    holder.itemUserMenuMapImage.set
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageModel> call, Throwable t) {
                        Log.e(UserMenuRecyclerAdapter.class.getName(), "Error responce image model: " + t.getMessage(), t);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static final class UserMenuRecyclerViewHolder extends RecyclerView.ViewHolder {
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
        LinearLayout trackLinearLayout;
        FragmentManager fragmentManager;

        public void setFragmentManager(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }


        public UserMenuRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            this.userPicItemUserMenu = itemView.findViewById(R.id.userPicItemUserMenu);
            this.addFavoriteTrackButton = itemView.findViewById(R.id.addFavoriteTrackButton);
            this.addImportantTrack = itemView.findViewById(R.id.addImportantTrack);
            this.chatAltFillButton = itemView.findViewById(R.id.chatAltFillButton);
            this.uploadTrackButton = itemView.findViewById(R.id.shareTrackButton);
            this.firstAndLastnameUserMenuItem = itemView.findViewById(R.id.firstAndLastnameUserMenuItem);
            this.dateUserMenuItem = itemView.findViewById(R.id.dateUserMenuItem);
            this.distanceTextViewUserMenu = itemView.findViewById(R.id.distanceTextViewUserMenu);
            this.avgSpeedTextViewUserMenu = itemView.findViewById(R.id.avgSpeedTextViewUserMenu);
            this.timeTextViewUserMenu = itemView.findViewById(R.id.timeTextViewUserMenu);
            this.itemUserMenuMapImage = itemView.findViewById(R.id.itemUserMenuMapImage);
            this.trackLinearLayout = itemView.findViewById(R.id.trackLinearLayout);
            this.trackLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open route
                    fragmentManager.beginTransaction()
                            .hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("user_menu")))
                            .show(Objects.requireNonNull(fragmentManager.findFragmentByTag("openTrack")))
                            .commit();;
                }
            });
        }
    }

}
