package ru.netcracker.bikepacker.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.ImageSliderAdapter;

public class ConfirmEmailFragment extends Fragment {
    Button repeat;
    SliderView imageSlider;
    View view;
    ImageSliderAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_confirm_email, container, false);
        repeat = view.findViewById(R.id.buttonRepeatConfirmEmail);
        imageSlider = view.findViewById(R.id.confirmEmailFragmentImageSlider);
        ArrayList<String> images = new ArrayList<>();
        images.add("https://bikepacking.com/wp-content/uploads/2022/05/Old-Man-Mountain-Elkhorn-Rack-Review_4.jpg");
        images.add("https://bikepacking.com/wp-content/uploads/2022/05/Old-Man-Mountain-Elkhorn-Rack-Review_9.jpg");
        images.add("https://bikepacking.com/wp-content/uploads/2022/05/Old-Man-Mountain-Elkhorn-Rack-Review_22.jpg");
        images.add("https://bikepacking.com/wp-content/uploads/2022/05/Old-Man-Mountain-Elkhorn-Rack-Review_19.jpg");
        adapter = new ImageSliderAdapter(this.getContext(), images);
        imageSlider.setSliderAdapter(adapter);
        imageSlider.setIndicatorEnabled(false);
        imageSlider.setIndicatorRadius(0);
        //imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        //imageSlider.setIndicatorSelectedColor(Color.BLUE);
        //imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        imageSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
        imageSlider.startAutoCycle();

        return view;
    }
}