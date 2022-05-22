package ru.netcracker.bikepacker.view;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.service.GpxFileManager;
import ru.netcracker.bikepacker.service.ZipFileManager;


public class UserMenuInformationAccountFragment extends Fragment {
    private View userMenuInformationAccountFragmentView;
    private ImageView userPic;
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

        this.userMenuInformationAccountFragmentView = inflater.inflate(R.layout.fragment_user_menu_information_account, container, false);
        this.userAccountManager = UserAccountManager.getInstance(getContext());
        this.userPic = userMenuInformationAccountFragmentView.findViewById(R.id.userPicInUserMenu);
        this.firstAndLastNames = userMenuInformationAccountFragmentView.findViewById(R.id.firstAndLastNamesInUserMenu);
        this.nickname = userMenuInformationAccountFragmentView.findViewById(R.id.nicknameInUserMenu);
        this.email = userMenuInformationAccountFragmentView.findViewById(R.id.emailInUserMenu);
        this.editButton = userMenuInformationAccountFragmentView.findViewById(R.id.editButtonInformationAccountUserMenuFragment);
        this.contentResolver = requireActivity().getContentResolver();
        if (!userAccountManager.getUser().getUserPicLink().isEmpty()) {
            Picasso.get()
                    .load(userAccountManager.getUser().getUserPicLink())
                    .placeholder(R.drawable.ic_userpic)
                    .error(R.drawable.ic_userpic)
                    .into(userPic);
        }
        fileManager = new GpxFileManager(requireContext());

        firstAndLastNames.setText(userAccountManager.getUser().getFirstname() + " " + userAccountManager.getUser().getLastname());
        nickname.setText(userAccountManager.getUser().getUsername());
        email.setText(userAccountManager.getUser().getEmail());
        ImageButton importButton = userMenuInformationAccountFragmentView.findViewById(R.id.importButton);
        ZipFileManager zipFileManager = new ZipFileManager(requireContext());
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zipFileManager.openUploadFileDialog(requireContext());
            }
        });

        return userMenuInformationAccountFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }
}