package ru.netcracker.bikepacker.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.tracks.listeners.OnStartRouteBtnListener;

public class OpenTrackFragment extends Fragment {
    private Button buttonStartRoute;
    OnStartRouteBtnListener onStartRouteBtnListener;

    public void setOnStartRouteBtnListener(OnStartRouteBtnListener onStartRouteBtnListener) {
        this.onStartRouteBtnListener = onStartRouteBtnListener;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private FragmentManager fragmentManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonStartRoute = view.findViewById(R.id.startRouteButton);
        buttonStartRoute.setOnClickListener(view1 -> onStartRouteBtnListener.onClick());
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
