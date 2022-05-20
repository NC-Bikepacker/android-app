package ru.netcracker.bikepacker.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.homemenu.HomeMenuRecyclerAdapter;
import ru.netcracker.bikepacker.model.NewsCardModel;
import ru.netcracker.bikepacker.service.ImageConverter;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageRecyclerViewHolder>{

    private Context context;
    private List<String> images;
    private ImageConverter imageConverter;

    public ImageRecyclerAdapter(Context context, List<String> images) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageRecyclerAdapter.ImageRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageItem = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageRecyclerAdapter.ImageRecyclerViewHolder(imageItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ImageRecyclerAdapter.ImageRecyclerViewHolder holder, int position) {
        Picasso.get()
                .load(images.get(position))
                .placeholder(R.drawable.image_bikepacker)
                .centerCrop().resize(800,600)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static final class ImageRecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ImageRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.itemImage);
        }

    }
}
