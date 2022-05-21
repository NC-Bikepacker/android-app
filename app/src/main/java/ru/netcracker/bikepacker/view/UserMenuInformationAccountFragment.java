package ru.netcracker.bikepacker.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Optional;

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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.userMenuInformationAccountFragmentView =  inflater.inflate(R.layout.fragment_user_menu_information_account, container, false);
        this.userAccountManager = UserAccountManager.getInstance(getContext());
        this.userPic = userMenuInformationAccountFragmentView.findViewById(R.id.userPicInUserMenu);
        this.firstAndLastNames = userMenuInformationAccountFragmentView.findViewById(R.id.firstAndLastNamesInUserMenu);
        this.nickname = userMenuInformationAccountFragmentView.findViewById(R.id.nicknameInUserMenu);
        this.email = userMenuInformationAccountFragmentView.findViewById(R.id.emailInUserMenu);
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
                            logout();
                            return true;
                        case R.id.edit_userpic:
                                editUserpic();
                            return true;
                        case R.id.edit_profile_button:
                            editAccount();
                            return true;
                        case R.id.import_strava_button:
                            importFromStrava();
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

    private void logout(){
        UserAccountManager.getInstance(requireContext()).removeUser();
        SessionManager.getInstance(requireContext()).removeSession();
        Intent intent = new Intent(requireActivity(), StartingAppActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void editUserpic(){
        Toast.makeText(requireContext(), "тут должно быть окно смены изображения", Toast.LENGTH_LONG).show();
    }

    private void editAccount(){
        Optional<Fragment> activeFragment = Optional.ofNullable(MainNavigationActivity.Companion.getActiveFragment());
        AccountEditorFragment accountEditorFragment = new AccountEditorFragment();
        if(activeFragment.isPresent()){
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, accountEditorFragment, "TAG_EDIT_ACCOUNT")
                    .hide(activeFragment.get())
                    .show(accountEditorFragment)
                    .commit();
            MainNavigationActivity.Companion.setActiveFragment(accountEditorFragment);
        }
    }

    private void importFromStrava(){
        Toast.makeText(requireContext(), "тут должно быть окно импорта из Стравы", Toast.LENGTH_LONG).show();
    }

}