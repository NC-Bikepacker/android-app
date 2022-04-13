package ru.netcracker.bikepacker.view;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.manager.UserAccountManager;


public class UserMenuInformationAccountFragment extends Fragment {
    private View userMenuInformationAccountFragmentView;
    private ImageView userPic;
    private TextView firstAndLastNames, email, nickname;
    private Button editButton;
    private UserAccountManager userAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.userMenuInformationAccountFragmentView =  inflater.inflate(R.layout.fragment_user_menu_information_account, container, false);
        this.userAccountManager = UserAccountManager.getInstance(getContext());
        this.userPic = userMenuInformationAccountFragmentView.findViewById(R.id.userPicInUserMenu);
        this.firstAndLastNames = userMenuInformationAccountFragmentView.findViewById(R.id.firstAndLastNamesInUserMenu);
        this.nickname = userMenuInformationAccountFragmentView.findViewById(R.id.nicknameInUserMenu);
        this.email = userMenuInformationAccountFragmentView.findViewById(R.id.emailInUserMenu);
        this.editButton = userMenuInformationAccountFragmentView.findViewById(R.id.editButtonInformationAccountUserMenuFragment);

        Picasso.get()
                .load(userAccountManager.getUser().getUserPicLink())
                .placeholder(R.drawable.ic_userpic)
                .error(R.drawable.ic_userpic)
                .into(userPic);

        firstAndLastNames.setText(userAccountManager.getUser().getFirstname() + " " + userAccountManager.getUser().getLastname());
        nickname.setText(userAccountManager.getUser().getUsername());
        email.setText(userAccountManager.getUser().getEmail());

        return userMenuInformationAccountFragmentView;
    }

}