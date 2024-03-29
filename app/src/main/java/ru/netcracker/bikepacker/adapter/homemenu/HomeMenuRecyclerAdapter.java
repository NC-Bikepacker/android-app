package ru.netcracker.bikepacker.adapter.homemenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.ImageRecyclerAdapter;
import ru.netcracker.bikepacker.adapter.ImageSliderAdapter;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.NewsCardModel;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.model.UserModel;
import ru.netcracker.bikepacker.service.ImageConverter;

public class HomeMenuRecyclerAdapter extends RecyclerView.Adapter<HomeMenuRecyclerAdapter.HomeMenuRecyclerViewHolder>{

    private Context context;
    private List<NewsCardModel> news;
    private RetrofitManager retrofitManager;
    private UserAccountManager userAccountManager;
    private ImageConverter imageConverter;

    public HomeMenuRecyclerAdapter(Context context, List<NewsCardModel> news) {
        this.context = context;
        this.news = news;
        this.retrofitManager = RetrofitManager.getInstance(context);
        this.userAccountManager = UserAccountManager.getInstance(context);
        this.imageConverter = new ImageConverter();
    }

    @NonNull
    @Override
    public HomeMenuRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View newsItem = LayoutInflater.from(context).inflate(R.layout.item_newspaper_card, parent, false);
        return new HomeMenuRecyclerAdapter.HomeMenuRecyclerViewHolder(newsItem);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull HomeMenuRecyclerViewHolder holder, int position) {
        NewsCardModel newsCardModel = news.get(position);
        UserModel user = newsCardModel.getUser();
        TrackModel track = newsCardModel.getTrack();
        List<String> images = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        ImageSliderAdapter adapter;

        images.add("https://bikepacking.com/wp-content/uploads/2022/05/Old-Man-Mountain-Elkhorn-Rack-Review_4.jpg");
        images.add("https://bikepacking.com/wp-content/uploads/2022/05/Old-Man-Mountain-Elkhorn-Rack-Review_9.jpg");
        images.add("https://bikepacking.com/wp-content/uploads/2022/05/Old-Man-Mountain-Elkhorn-Rack-Review_22.jpg");
        images.add("https://bikepacking.com/wp-content/uploads/2022/05/Old-Man-Mountain-Elkhorn-Rack-Review_19.jpg");

        Picasso.get()
                .load(track.getUser().getUserPicLink())
                .placeholder(R.drawable.ic_userpic)
                .error(R.drawable.ic_userpic)
                .into(holder.userPicItemHomeMenu);

        holder.usernameHomeMenuItem.setText(newsCardModel.getUser().getUsername());
        holder.firstAndLastnameHomeMenuItem.setText(String.format("%s %s", user.getFirstname(), user.getLastname()));
        holder.bodyDescriptionTextView.setText(newsCardModel.getDescription());
        holder.bodyDescriptionTextView.setOnClickListener(v -> holder.bodyDescriptionTextView.setMaxLines(Integer.MAX_VALUE));
        holder.distanceTextViewNewspaperCard.setText("12");
        holder.complexityTextViewNewspaperCard.setText(Double.toString(track.getTrackComplexity()));

        holder.timeTextViewNewspaperCard.setText(convertTravelTime(track));
        holder.dateHomeMenuItem.setText(newsCardModel.getDate());

        if(track.getImageBase64()!=null) {
            holder.imageTrackItemNewspaperCard.setImageBitmap(imageConverter.decode(track.getImageBase64()));
        }
        adapter = new ImageSliderAdapter(context, images);
        holder.imageSliderView.setSliderAdapter(adapter);
        holder.imageSliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        holder.imageSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        holder.imageSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        holder.imageSliderView.setIndicatorSelectedColor(Color.WHITE);
        holder.imageSliderView.setIndicatorUnselectedColor(Color.GRAY);
        holder.imageSliderView.setScrollTimeInSec(3); //set scroll delay in seconds :
        holder.imageSliderView.startAutoCycle();

        holder.likeNewspaperCard.setOnClickListener(v -> likeNewsCard(holder));
        holder.likeCountTextViewNewspaperCard.setText(String.valueOf(newsCardModel.getLikes()));
    }

    private ImageRecyclerAdapter getImageRecyclerAdapter(List<String> images){
        return new ImageRecyclerAdapter(context, images);
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public static final class HomeMenuRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView userPicItemHomeMenu,
                imageTrackItemNewspaperCard;
        ImageButton likeNewspaperCard,
                chatAltFillButtonOnNewspaperCard,
                shareNewspaperCardButton;
        TextView firstAndLastnameHomeMenuItem,
                usernameHomeMenuItem,
                bodyDescriptionTextView,
                distanceTextViewNewspaperCard,
                complexityTextViewNewspaperCard,
                timeTextViewNewspaperCard,
                dateHomeMenuItem,
                likeCountTextViewNewspaperCard;
        SliderView imageSliderView;


        public HomeMenuRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            this.userPicItemHomeMenu = itemView.findViewById(R.id.userPicItemHomeMenu);
            this.firstAndLastnameHomeMenuItem = itemView.findViewById(R.id.firstAndLastnameHomeMenuItem);
            this.usernameHomeMenuItem = itemView.findViewById(R.id.nicknameHomeMenuItem);
            this.bodyDescriptionTextView = itemView.findViewById(R.id.bodyDescriptionTextView);
            this.imageSliderView = itemView.findViewById(R.id.imageSlider);
            this.distanceTextViewNewspaperCard = itemView.findViewById(R.id.distanceTextViewNewspaperCard);
            this.complexityTextViewNewspaperCard = itemView.findViewById(R.id.complexityTextViewNewspaperCard);
            this.timeTextViewNewspaperCard = itemView.findViewById(R.id.timeTextViewNewspaperCard);
            this.dateHomeMenuItem = itemView.findViewById(R.id.dateHomeMenuItem);
            this.likeNewspaperCard = itemView.findViewById(R.id.likeNewspaperCard);
            this.likeNewspaperCard.setTag(R.drawable.ic_favorite_fill);
            this.chatAltFillButtonOnNewspaperCard = itemView.findViewById(R.id.chatAltFillButtonOnNewspaperCard);
            this.shareNewspaperCardButton = itemView.findViewById(R.id.shareNewspaperCardButton);
            this.imageTrackItemNewspaperCard = itemView.findViewById(R.id.imageTrackItemNewspaperCard);
            this.likeCountTextViewNewspaperCard = itemView.findViewById(R.id.likeCountTextViewNewspaperCard);
        }

    }

    /*Convert travel time in seconds to readable string in format HH:MM:SS*/
    private String convertTravelTime(TrackModel track){
        long travelTime = track.getTravelTime();
        long sec = travelTime % 60;
        long min = (travelTime / 60) % 60;
        long hours = (travelTime / 60) / 60;

        String trTimeSeconds = (sec < 10) ? "0" + sec : Long.toString(sec);
        String trTimeMinutes = (min < 10) ? "0" + min : Long.toString(min);
        String trTimeHours = (hours < 10) ? "0" + hours : Long.toString(hours);

        return trTimeHours + ":" + trTimeMinutes + ":" + trTimeSeconds;
    }

    @SuppressLint("ResourceAsColor")
    private void likeNewsCard(HomeMenuRecyclerViewHolder holder){
        TextView likeCount = holder.likeCountTextViewNewspaperCard;
        String likeCountText = String.valueOf(likeCount.getText());
        if(holder.likeNewspaperCard.getTag().equals(R.drawable.ic_favorite_fill_red)){
            holder.likeNewspaperCard.setImageResource(R.drawable.ic_favorite_fill);
            holder.likeNewspaperCard.setTag(R.drawable.ic_favorite_fill);
            holder.likeCountTextViewNewspaperCard.setText(String.valueOf(Long.parseLong(likeCountText)-1));
        }else {
            holder.likeNewspaperCard.setImageResource(R.drawable.ic_favorite_fill_red);
            holder.likeNewspaperCard.setTag(R.drawable.ic_favorite_fill_red);
            holder.likeCountTextViewNewspaperCard.setText(String.valueOf(Long.parseLong(likeCountText)+1));
        }
    }
}


