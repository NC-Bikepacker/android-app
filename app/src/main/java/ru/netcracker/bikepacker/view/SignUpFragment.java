package ru.netcracker.bikepacker.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.databinding.FragmentSignInBinding;
import ru.netcracker.bikepacker.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding fragmentSignUpBinding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        fragmentSignUpBinding = FragmentSignUpBinding.inflate(inflater, container, false);
        return fragmentSignUpBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentSignUpBinding.fragmentSignUpButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO: Handle click
                NavHostFragment.findNavController(SignUpFragment.this)
                        .navigate(R.id.action_signUpFragment_to_mapPage);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSignUpBinding = null;
    }
}
