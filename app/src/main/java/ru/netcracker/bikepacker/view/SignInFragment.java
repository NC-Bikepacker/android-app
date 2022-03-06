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

public class SignInFragment extends Fragment {

    private FragmentSignInBinding fragmentSignInBinding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        fragmentSignInBinding = FragmentSignInBinding.inflate(inflater, container, false);
        return fragmentSignInBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        fragmentSignInBinding.fragmentSignUpButtonSignUp.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(SignInFragment.this)
                    .navigate(R.id.action_signInFragment_to_mapPage);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSignInBinding = null;
    }
}
