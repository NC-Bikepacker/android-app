package ru.netcracker.bikepacker.view;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.SessionManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.UserModel;
import ru.netcracker.bikepacker.service.GpxFileManager;


public class UserMenuInformationAccountFragment extends Fragment {

    private View userMenuInformationAccountFragmentView;
    private ImageView userPic, popupMenuSettingButton;
    private TextView firstAndLastNames, email, nickname;
    private Button editButton;
    private UserAccountManager userAccountManager;
    private GpxFileManager fileManager;
    private ContentResolver contentResolver;

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
        this.editButton = userMenuInformationAccountFragmentView.findViewById(R.id.editButtonInformationAccountUserMenuFragment);
        this.contentResolver = requireActivity().getContentResolver();
        this.popupMenuSettingButton = userMenuInformationAccountFragmentView.findViewById(R.id.popupMenuButtonUserMenu);
        if (!userAccountManager.getUser().getUserPicLink().isEmpty()) {
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

        fileManager = new GpxFileManager(requireContext());
        firstAndLastNames.setText(userAccountManager.getUser().getFirstname() + " " + userAccountManager.getUser().getLastname());
        nickname.setText(userAccountManager.getUser().getUsername());
        email.setText(userAccountManager.getUser().getEmail());
        ImageButton importButton = userMenuInformationAccountFragmentView.findViewById(R.id.importButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment=new DialogFragment();
                dialogFragment.show(requireActivity().getSupportFragmentManager(),"DialogFragment");
            }
        });
        return userMenuInformationAccountFragmentView;
    }

    private void logout(){
        UserAccountManager.getInstance(requireContext()).removeUser();
        SessionManager.getInstance(requireContext()).removeSession();
        Intent intent = new Intent(requireActivity(), StartingAppActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    //метод для изменения изображения юзера
    private void editUserpic(){
    }

    //метод для вызова фрагмента с полями для редактирования данных юзера
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

    //метод для импорта данных из Стравы
    private void importFromStrava(){

    }

    //метод для обновления полей в фрагменте после обновления данных юзера
    private void updateUserData(){
        RetrofitManager.getInstance(getContext()).getJSONApi()
                .getUserWithID(userAccountManager.getCookie(), userAccountManager.getUser().getId())
                .enqueue(new Callback<UserModel>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            SessionManager.getInstance(requireContext()).setSessionUser(response.body());
                            userAccountManager.updateUserData();
                            firstAndLastNames.setText(userAccountManager.getUser().getFirstname() + " " + userAccountManager.getUser().getLastname());
                            nickname.setText(userAccountManager.getUser().getUsername());
                            email.setText(userAccountManager.getUser().getEmail());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                        Log.e("User<enuInformationFragment", t.getMessage(), t);
                    }
                });


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }
}