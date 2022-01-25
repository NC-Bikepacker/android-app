package ru.netcracker.bikepacker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import ru.netcracker.bikepacker.adapter.FindFriendAdapter;
import ru.netcracker.bikepacker.model.FindFriendEntity;


public class FindFriendFragment extends Fragment {

    List<FindFriendEntity> findFriendsList = new ArrayList<>();
    RecyclerView friendsRecyclerView;
    FindFriendAdapter friendAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_friend, container, false);
        findFriendsList.add(new FindFriendEntity(1,"Alex", "Alexov", "alexoff_123", "https://i.pinimg.com/736x/61/4f/9a/614f9ab2b8ea893cea52a8bc3e8d44e8.jpg"));
        findFriendsList.add(new FindFriendEntity(2,"Alex", "Alexov", "alexoff_123", "https://i.pinimg.com/736x/61/4f/9a/614f9ab2b8ea893cea52a8bc3e8d44e8.jpg"));
        setFindFragmentRecycler(findFriendsList, view);
        return view;
    }

    private void setFindFragmentRecycler(List<FindFriendEntity> findFriendsList, View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        friendsRecyclerView = view.findViewById(R.id.findFriendRecycler);
        friendsRecyclerView.setLayoutManager(layoutManager);

        friendAdapter = new FindFriendAdapter(getContext(), findFriendsList);
        friendsRecyclerView.setAdapter(friendAdapter);
    }


}