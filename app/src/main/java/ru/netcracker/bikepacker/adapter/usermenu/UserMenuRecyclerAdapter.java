package ru.netcracker.bikepacker.adapter.usermenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.MapView;
import com.squareup.picasso.Picasso;

import java.util.List;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.model.TrackDTO;

public class UserMenuRecyclerAdapter extends RecyclerView.Adapter<UserMenuRecyclerAdapter.UserMenuRecyclerViewHolder>{

    private Context context;
    private List<TrackDTO> tracks;

    public UserMenuRecyclerAdapter(Context context, List<TrackDTO> tracks) {
        this.context = context;
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public UserMenuRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tracksItem = LayoutInflater.from(context).inflate(R.layout.item_track_user_menu, parent, false);
        return new UserMenuRecyclerAdapter.UserMenuRecyclerViewHolder(tracksItem);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMenuRecyclerViewHolder holder, int position) {
        TrackDTO track = tracks.get(position);

        Picasso.get()
                .load(track.getUser().getUserPic_url())
                .placeholder(R.drawable.ic_userpic)
                .error(R.drawable.ic_userpic)
                .into(holder.userPicItemUserMenu);

        holder.firstAndLastnameUserMenuItem.setText(tracks.get(position).getUser().getFirstName() +
                                                    " " +
                                                    tracks.get(position).getUser().getLastName());
        holder.timeTextViewUserMenu.setText(String.valueOf(tracks.get(position).getTravelTime()));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static final class UserMenuRecyclerViewHolder extends RecyclerView.ViewHolder{
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
        MapView itemUserMenuMapView;


        public UserMenuRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            this.userPicItemUserMenu = itemView.findViewById(R.id.userPicItemUserMenu);
            this.addFavoriteTrackButton = itemView.findViewById(R.id.addFavoriteTrackButton);
            this.addImportantTrack = itemView.findViewById(R.id.addImportantTrack);
            this.chatAltFillButton = itemView.findViewById(R.id.chatAltFillButton);
            this.uploadTrackButton = itemView.findViewById(R.id.uploadTrackButton);
            this.firstAndLastnameUserMenuItem = itemView.findViewById(R.id.firstAndLastnameUserMenuItem);
            this.dateUserMenuItem = itemView.findViewById(R.id.dateUserMenuItem);
            this.trackNameTextView = itemView.findViewById(R.id.trackNameTextView);
            this.distanceTextViewUserMenu = itemView.findViewById(R.id.distanceTextViewUserMenu);
            this.avgSpeedTextViewUserMenu = itemView.findViewById(R.id.avgSpeedTextViewUserMenu);
            this.timeTextViewUserMenu = itemView.findViewById(R.id.timeTextViewUserMenu);
            this.itemUserMenuMapView = itemView.findViewById(R.id.itemUserMenuMapView);
        }
    }

}
