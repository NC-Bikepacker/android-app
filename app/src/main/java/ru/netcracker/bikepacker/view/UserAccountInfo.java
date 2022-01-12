package ru.netcracker.bikepacker.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.w3c.dom.Text;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.databinding.FragmentLogInBinding;
import ru.netcracker.bikepacker.databinding.FragmentUserAccountInfoBinding;
import ru.netcracker.bikepacker.model.Email;
import ru.netcracker.bikepacker.model.Password;

public class UserAccountInfo extends Fragment {

    private @NonNull
    FragmentUserAccountInfoBinding fragmentUserAccountInfoBinding;

    ImageView userpic;
    TextView fullname;
    TextView username;
    TextView email;
    Button editUserProfileButton;

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

        editUserProfileButton = fragmentUserAccountInfoBinding.fragmentUserAccountInfoButtonEditProfile;
        userpic = fragmentUserAccountInfoBinding.fragmentUserAccountInfoUserPicImageView;
        fullname = fragmentUserAccountInfoBinding.fragmentUserAccountInfoFullname;
        username = fragmentUserAccountInfoBinding.fragmentUserAccountInfoUsername;
        email = fragmentUserAccountInfoBinding.fragmentUserAccountInfoEmail;

//        userpic.setBackgroundResource(R.mipmap.ic_launcher_round);
        fullname.setText("Александр Рязанцев");
        username.setText("ad-ryaz");
        email.setText("ad-ryaz@yandex.ru");

        editUserProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserAccountInfo.this)
                        .navigate(R.id.action_userAccountInfo_to_editUserProfile);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentUserAccountInfoBinding = null;
    }
}
