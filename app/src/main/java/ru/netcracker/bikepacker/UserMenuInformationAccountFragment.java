package ru.netcracker.bikepacker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class UserMenuInformationAccountFragment extends Fragment {
    View userMenuInformationAccountFragmentView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.userMenuInformationAccountFragmentView =  inflater.inflate(R.layout.fragment_user_menu_information_account, container, false);

        return userMenuInformationAccountFragmentView;
    }
}