package ru.netcracker.bikepacker.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.SessionManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.SignUpModel;
import ru.netcracker.bikepacker.model.UserModel;
import ru.netcracker.bikepacker.service.PasswordGeneratingService;

public class AccountEditorFragment extends Fragment {
    private View viewAccountEditor;
    private TextInputEditText   firstname,
                                lastname,
                                email,
                                username,
                                password,
                                confirmPassword;

    private ImageButton backFragment;
    private Button save;
    private UserAccountManager userAccountManager;
    private SignUpModel user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialization
        viewAccountEditor = inflater.inflate(R.layout.fragment_account_editor, container, false);
        firstname = viewAccountEditor.findViewById(R.id.firstnameTextInputAccountEditor);
        lastname = viewAccountEditor.findViewById(R.id.lastnameTextInputAccountEditor);
        email = viewAccountEditor.findViewById(R.id.emailTextInputAccountEditor);
        username = viewAccountEditor.findViewById(R.id.usernameTextInputAccountEditor);
        password = viewAccountEditor.findViewById(R.id.passwordTextInputAccountEditor);
        confirmPassword = viewAccountEditor.findViewById(R.id.confirmPasswordTextInputAccountEditor);
        save = viewAccountEditor.findViewById(R.id.saveButtonAccountEditorFragment);
        backFragment = viewAccountEditor.findViewById(R.id.backToUserAccountFragment);
        userAccountManager = UserAccountManager.getInstance(getContext());


        //setting field values
        firstname.setText(userAccountManager.getUser().getFirstname());
        lastname.setText(userAccountManager.getUser().getLastname());
        email.setText(userAccountManager.getUser().getEmail());
        username.setText(userAccountManager.getUser().getUsername());

        Optional<Editable> firstnameOpt = Optional.ofNullable(firstname.getText());
        Optional<Editable> lastnameOpt = Optional.ofNullable(lastname.getText());
        Optional<Editable> emailOpt = Optional.ofNullable(email.getText());
        Optional<Editable> usernameOpt = Optional.ofNullable(username.getText());


        save.setOnClickListener(view -> {
            user = new SignUpModel();
            //флаг изменений, если есть изменения, то true
            boolean editFlag = false;

            //проверка, что поле в фрагменте изменено и не является нулевым
            if(firstnameOpt.isPresent() && !userAccountManager.getUser().getFirstname().equals(firstnameOpt.get().toString()) && !firstnameOpt.get().toString().isEmpty()) { editFlag=true; }
            if(lastnameOpt.isPresent() && !userAccountManager.getUser().getLastname().equals(lastnameOpt.get().toString()) && !lastnameOpt.get().toString().isEmpty()){ editFlag=true;}
            if(emailOpt.isPresent() && !userAccountManager.getUser().getEmail().equals(emailOpt.get().toString()) && !emailOpt.get().toString().isEmpty()){editFlag=true;}
            if(usernameOpt.isPresent() && !userAccountManager.getUser().getUsername().equals(usernameOpt.get().toString()) && !usernameOpt.get().toString().isEmpty()){editFlag=true;}


            //устанавливаем поля для новых данных юзера
            user.setFirstname(firstname.getText().toString());
            user.setLastname(lastname.getText().toString());
            user.setEmail(email.getText().toString());
            user.setUsername(username.getText().toString());

            if(password.getText()!=null && confirmPassword.getText()!=null){
                if(password.getText().toString().length() >= 8 &&
                        password.getText().toString().equals(Objects.requireNonNull(confirmPassword.getText()).toString()) &&
                        PasswordGeneratingService.isValidPassword(password.getText().toString())){
                    user.setPassword(password.getText().toString());
                    editFlag = true;
                }
                else {
                    editFlag = false;

                    if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                        Toast.makeText(getContext(), "введенные пароли не совпадают, повторите попытку ввода", Toast.LENGTH_LONG).show();
                        password.setText("");
                        confirmPassword.setText("");
                        return;
                    }
                    if (password.getText().toString().length() < 8) {
                        Toast.makeText(getContext(), "длинна пароля менее 8 символов, введите пароль удовлетворяющий условиям безопасности", Toast.LENGTH_LONG).show();
                        password.setText("");
                        confirmPassword.setText("");
                        return;
                    }
                    if (!PasswordGeneratingService.isValidPassword(password.getText().toString())) {
                        Toast.makeText(getContext(), "введенный пароль не соответствует условиям безопасности, введите пароль содержащий хотя бы одну цифру, одну заглавную букву и одну строчную букву", Toast.LENGTH_LONG).show();
                        password.setText("");
                        confirmPassword.setText("");
                        return;
                    }
                }
            }

            if (editFlag){
                RetrofitManager.getInstance(getContext())
                        .getJSONApi()
                        .updateUserData(userAccountManager.getCookie(), user)
                        .enqueue(new Callback<UserModel>() {
                            @Override
                            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                                if (response.isSuccessful() && response.body()!=null){
                                    SessionManager.getInstance(getContext())
                                            .setSessionUser(response.body());
                                    UserAccountManager.getInstance(getContext());
                                    Toast.makeText(getContext(), "Данные аккаунта успешно обновлены", Toast.LENGTH_SHORT).show();
                                    password.setText("");
                                    confirmPassword.setText("");
                                }
                                else {
                                    Log.e("AccountEditorFragment.class", "Не выполнен запрос отправки данных аккаунта клиента");
                                    Toast.makeText(getContext(), "Проверьте соединение интернет.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                                Log.e("AccountEditorFragment.class", "Ошибка выполнения запроса отправки данных аккаунта клиента");
                                Toast.makeText(getContext(), "Ошибка запроса отправки данных", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        backFragment.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            Optional<Fragment> activeFragment = Optional.ofNullable(MainNavigationActivity.Companion.getActiveFragment());
            if(activeFragment.isPresent()){
                fragmentManager.beginTransaction()
                        .hide(activeFragment.get())
                        .show(Objects.requireNonNull(fragmentManager.findFragmentByTag("user_menu")))
                        .commit();
                MainNavigationActivity.Companion.setActiveFragment(fragmentManager.findFragmentByTag("user_menu"));
            }
        });
        return viewAccountEditor;
    }
}