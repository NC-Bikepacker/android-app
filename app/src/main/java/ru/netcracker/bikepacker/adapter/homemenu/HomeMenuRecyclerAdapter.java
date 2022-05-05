package ru.netcracker.bikepacker.adapter.homemenu;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.FindFriendAdapter;
import ru.netcracker.bikepacker.adapter.ImageRecyclerAdapter;
import ru.netcracker.bikepacker.adapter.ImageSliderAdapter;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.ImageModel;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull HomeMenuRecyclerViewHolder holder, int position) {
        //Toast.makeText(context, news.size(), Toast.LENGTH_SHORT).show();
        NewsCardModel newsCardModel = news.get(position);
        UserModel user = newsCardModel.getUser();
        TrackModel track = newsCardModel.getTrack();
        List<ImageModel> images = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        ImageSliderAdapter adapter;
        Random random = new Random();

        ////////////////////////////
        images = newsCardModel.getImages();
        ///////////////////////////////

        Picasso.get()
                .load(track.getUser().getUserPicLink())
                .placeholder(R.drawable.ic_userpic)
                .error(R.drawable.ic_userpic)
                .into(holder.userPicItemHomeMenu);

        holder.usernameHomeMenuItem.setText(newsCardModel.getUser().getUsername());
        holder.firstAndLastnameHomeMenuItem.setText(user.getFirstname() + " " + user.getLastname());
        holder.bodyDescriptionTextView.setText(newsCardModel.getDescription());
        holder.distanceTextViewNewspaperCard.setText("12");
        holder.complexityTextViewNewspaperCard.setText(Long.toString(track.getTrackComplexity()));
        holder.timeTextViewNewspaperCard.setText("00:43:14");
        holder.dateHomeMenuItem.setText(newsCardModel.getDate());

        if(track.getImageBase64()!=null) {
            holder.imageTrackItemNewspaperCard.setImageBitmap(imageConverter.decode(track.getImageBase64()));
        }
//        holder.imagesNewspaperCardRecycleView.setLayoutManager(layoutManager);
//        holder.imagesNewspaperCardRecycleView.setAdapter(getImageRecyclerAdapter(images));

        adapter = new ImageSliderAdapter(context, images);
        holder.imageSliderView.setSliderAdapter(adapter);
        holder.imageSliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        holder.imageSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        holder.imageSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        holder.imageSliderView.setIndicatorSelectedColor(Color.WHITE);
        holder.imageSliderView.setIndicatorUnselectedColor(Color.GRAY);
        holder.imageSliderView.setScrollTimeInSec(3); //set scroll delay in seconds :
        holder.imageSliderView.startAutoCycle();
        //////////////////
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
                dateHomeMenuItem;
        RecyclerView imagesNewspaperCardRecycleView;
        SliderView imageSliderView;


        public HomeMenuRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            this.userPicItemHomeMenu = itemView.findViewById(R.id.userPicItemHomeMenu);
            this.firstAndLastnameHomeMenuItem = itemView.findViewById(R.id.firstAndLastnameHomeMenuItem);
            this.usernameHomeMenuItem = itemView.findViewById(R.id.nicknameHomeMenuItem);
            this.bodyDescriptionTextView = itemView.findViewById(R.id.bodyDescriptionTextView);
            //this.imagesNewspaperCardRecycleView = itemView.findViewById(R.id.imagesNuewspaperCardRecycleView);
            this.imageSliderView = itemView.findViewById(R.id.imageSlider);
            this.distanceTextViewNewspaperCard = itemView.findViewById(R.id.distanceTextViewNewspaperCard);
            this.complexityTextViewNewspaperCard = itemView.findViewById(R.id.complexityTextViewNewspaperCard);
            this.timeTextViewNewspaperCard = itemView.findViewById(R.id.timeTextViewNewspaperCard);
            this.dateHomeMenuItem = itemView.findViewById(R.id.dateHomeMenuItem);
            this.likeNewspaperCard = itemView.findViewById(R.id.likeNewspaperCard);
            this.chatAltFillButtonOnNewspaperCard = itemView.findViewById(R.id.chatAltFillButtonOnNewspaperCard);
            this.shareNewspaperCardButton = itemView.findViewById(R.id.shareNewspaperCardButton);
            this.imageTrackItemNewspaperCard = itemView.findViewById(R.id.imageTrackItemNewspaperCard);
        }

    }
}


