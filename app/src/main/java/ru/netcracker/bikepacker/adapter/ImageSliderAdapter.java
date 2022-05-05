package ru.netcracker.bikepacker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.model.ImageModel;
import ru.netcracker.bikepacker.service.ImageConverter;

public class ImageSliderAdapter extends
        SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<ImageModel> mSliderItems = new ArrayList<>();
    private ImageConverter imageConverter = new ImageConverter();

    public ImageSliderAdapter(Context context, List<ImageModel> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }

    public void renewItems(List<ImageModel> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(ImageModel sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider_layout, null);
        return new SliderAdapterVH(inflate);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

        ImageModel sliderItem = mSliderItems.get(position);

        viewHolder.imageViewBackground.setImageBitmap(imageConverter.decode(sliderItem.getImageBase64()));

       /* viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}