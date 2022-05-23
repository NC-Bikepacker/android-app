package ru.netcracker.bikepacker.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.netcracker.bikepacker.R;

public class CreateNewsCard extends Fragment {
    View viewCreateNewsCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewCreateNewsCard = inflater.inflate(R.layout.fragment_create_news_card, container, false);

        return viewCreateNewsCard;
    }
}