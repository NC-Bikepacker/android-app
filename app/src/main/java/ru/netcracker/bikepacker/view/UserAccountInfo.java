package ru.netcracker.bikepacker.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.databinding.FragmentUserAccountInfoBinding;
import ru.netcracker.bikepacker.manager.SessionManager;

public class UserAccountInfo extends Fragment {

    private @NonNull
    FragmentUserAccountInfoBinding fragmentUserAccountInfoBinding;

    ImageView userpic;
    TextView fullname;
    TextView username;
    TextView email;
    Button editUserProfileButton;
    Button logoutButton;

    Context context;
    SessionManager sessionManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        fragmentUserAccountInfoBinding = FragmentUserAccountInfoBinding.inflate(inflater, container, false);
        return fragmentUserAccountInfoBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        sessionManager = new SessionManager(context);

        editUserProfileButton = fragmentUserAccountInfoBinding.fragmentUserAccountInfoButtonEditProfile;
        logoutButton = fragmentUserAccountInfoBinding.fragmentUserAccountInfoButtonLogout;
        userpic = fragmentUserAccountInfoBinding.fragmentUserAccountInfoUserPicImageView;
        fullname = fragmentUserAccountInfoBinding.fragmentUserAccountInfoFullname;
        username = fragmentUserAccountInfoBinding.fragmentUserAccountInfoUsername;
        email = fragmentUserAccountInfoBinding.fragmentUserAccountInfoEmail;

        fullname.setText(sessionManager.getSessionUserFirstname() + " " + sessionManager.getSessionUserLastname());
        username.setText(sessionManager.getSessionUsername());
        email.setText(sessionManager.getSessionUserEmail());

        editUserProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserAccountInfo.this)
                        .navigate(R.id.action_userAccountInfo_to_editUserProfile);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.removeSession();
                System.out.println(sessionManager.getSessionId());
                NavHostFragment.findNavController(UserAccountInfo.this)
                        .navigate(R.id.logInFragment);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentUserAccountInfoBinding = null;
    }
}
