package ru.netcracker.bikepacker.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.manager.SessionManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;


public class UserMenuInformationAccountFragment extends Fragment {
    private View userMenuInformationAccountFragmentView;
    private ImageView userPic, popupMenuSettingButton;
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
        this.popupMenuSettingButton = userMenuInformationAccountFragmentView.findViewById(R.id.popupMenuButtonUserMenu);

        if(!userAccountManager.getUser().getUserPicLink().isEmpty()){
            Picasso.get()
                    .load(userAccountManager.getUser().getUserPicLink())
                    .placeholder(R.drawable.ic_userpic)
                    .error(R.drawable.ic_userpic)
                    .into(userPic);
        }

        popupMenuSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(requireContext(), popupMenuSettingButton);
                popup.getMenuInflater().inflate(R.menu.usermenu_information_account_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.logout:
                            UserAccountManager.getInstance(requireContext()).removeUser();
                            SessionManager.getInstance(requireContext()).removeSession();
                            Intent intent = new Intent(requireActivity(), StartingAppActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                            return true;
                        case R.id.edit_userpic:
                                Toast.makeText(requireContext(), "тут должно быть окно смены изображения", Toast.LENGTH_LONG).show();
                            return true;
                        default:
                            return false;
                    }
                });

                popup.show();
            }
        });


        firstAndLastNames.setText(userAccountManager.getUser().getFirstname() + " " + userAccountManager.getUser().getLastname());
        nickname.setText(userAccountManager.getUser().getUsername());
        email.setText(userAccountManager.getUser().getEmail());

        return userMenuInformationAccountFragmentView;
    }

}