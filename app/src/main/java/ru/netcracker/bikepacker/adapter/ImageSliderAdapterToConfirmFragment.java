package ru.netcracker.bikepacker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.service.ImageConverter;

public class ImageSliderAdapterToConfirmFragment extends
        SliderViewAdapter<ImageSliderAdapterToConfirmFragment.SliderAdapterVH> {

    private Context context;
    private List<Integer> mSliderItems;
    private ImageConverter imageConverter = new ImageConverter();

    public ImageSliderAdapterToConfirmFragment(Context context, List<Integer> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }

    public void renewItems(List<Integer> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Integer sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public ImageSliderAdapterToConfirmFragment.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider_layout, null);
        return new ImageSliderAdapterToConfirmFragment.SliderAdapterVH(inflate);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ImageSliderAdapterToConfirmFragment.SliderAdapterVH viewHolder, int position) {

        Integer sliderItem = mSliderItems.get(position);

        Picasso.get()
                .load(sliderItem)
                .fit().centerCrop()
                .into(viewHolder.imageViewBackground);
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
