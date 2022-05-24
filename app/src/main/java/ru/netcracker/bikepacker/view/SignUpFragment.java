package ru.netcracker.bikepacker.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.databinding.FragmentSignUpBinding;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.model.RoleEntity;
import ru.netcracker.bikepacker.model.SignUpModel;
import ru.netcracker.bikepacker.service.EmailValidationService;
import ru.netcracker.bikepacker.service.PasswordGeneratingService;

public class SignUpFragment extends Fragment {

    private @NonNull
    FragmentSignUpBinding fragmentSignUpBinding;
    private Context context;

    private EditText firstNameField;
    private EditText lastNameField;
    private EditText userNameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button submitFormButton;
    private TextView logInLink;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentSignUpBinding = FragmentSignUpBinding.inflate(inflater, container, false);
        context = getContext();
        return fragmentSignUpBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstNameField = fragmentSignUpBinding.layoutSignupFirstnameFieldTextField;
        lastNameField = fragmentSignUpBinding.layoutSignupLastnameFieldTextField;
        userNameField = fragmentSignUpBinding.layoutSignupUsernameFieldTextField;
        emailField = fragmentSignUpBinding.layoutSignupEmailFieldTextField;
        passwordField = fragmentSignUpBinding.layoutSignupPasswordFieldTextField;
        confirmPasswordField = fragmentSignUpBinding.layoutSignupConfirmPasswordFieldTextview;
        submitFormButton = fragmentSignUpBinding.fragmentSignUpButtonSignUp;
        logInLink = fragmentSignUpBinding.layoutSignupSignInLinkTextView;

        logInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SignUpFragment.this)
                        .navigate(R.id.action_signUpFragment_to_logInFragment);
            }
        });

        submitFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Optional firstnameFieldOpt = Optional.ofNullable(firstNameField.getText());
                Optional lastNameFieldOpt = Optional.ofNullable(lastNameField.getText());
                Optional usernameFieldOpt = Optional.ofNullable(userNameField.getText());
                Optional emailFieldOpt = Optional.ofNullable(emailField.getText());
                Optional passwordFieldOpt = Optional.ofNullable(passwordField.getText());
                Optional passConfirmationOpt = Optional.ofNullable(confirmPasswordField.getText());

                String firstname = StringUtils.capitalize(firstnameFieldOpt.orElseGet(() -> "").toString().trim().toLowerCase());
                String lastname = StringUtils.capitalize(lastNameFieldOpt.orElseGet(() -> "").toString().trim().toLowerCase());
                String username = usernameFieldOpt.orElseGet(() -> "").toString().trim().toLowerCase();
                String email = emailFieldOpt.orElseGet(() -> "").toString().trim();
                String pass = passwordFieldOpt.orElseGet(() -> "").toString();
                String passConfirmation = passConfirmationOpt.orElseGet(() -> "").toString();

                boolean emailIsValid = false;
                boolean passwordIsValid = false;
                boolean passwordIsConfirmed = false;
                boolean fieldsAreEmpty = firstname.equals("") || lastname.equals("") || username.equals("") || email.equals("") || pass.equals("") || passConfirmation.equals("");

                if (fieldsAreEmpty) {
                    Toast errorToast = Toast.makeText(getActivity().getApplicationContext(), "All field are required.", Toast.LENGTH_SHORT);
                    errorToast.show();
                } else {
                    emailIsValid = EmailValidationService.isEmailValid(email);

                    if (emailIsValid) {
                        passwordIsValid = pass.length() >= 8 && PasswordGeneratingService.isValidPassword(pass);

                        if (passwordIsValid) {
                            passwordIsConfirmed = pass.equals(passConfirmation);

                            if (!passwordIsConfirmed) {
                                Toast errorToast = Toast.makeText(getActivity().getApplicationContext(), "Password are not equal.", Toast.LENGTH_SHORT);
                                errorToast.show();
                            }
                        } else {
                            Toast errorToast = Toast.makeText(getActivity().getApplicationContext(), "This password is invalid or less then 8 characters.", Toast.LENGTH_SHORT);
                            errorToast.show();
                        }
                    } else {
                        Toast errorToast = Toast.makeText(getActivity().getApplicationContext(), "Email is not valid.", Toast.LENGTH_SHORT);
                        errorToast.show();
                    }
                }

                if (!fieldsAreEmpty && emailIsValid && passwordIsValid && passwordIsConfirmed) {
                    Toast errorToast = Toast.makeText(getActivity().getApplicationContext(), "You was successfully registered.", Toast.LENGTH_SHORT);
                    errorToast.show();

                    SignUpModel signUpModel = new SignUpModel();
                    signUpModel.setEmail(email);
                    signUpModel.setFirstname(firstname);
                    signUpModel.setLastname(lastname);
                    signUpModel.setUsername(username);
                    signUpModel.setPassword(pass);
                    signUpModel.setRoles(new RoleEntity(2L, "ROLE_USER"));
                    signUpModel.setAvatarImageUrl("no picture");

                    RetrofitManager.getInstance(context).getJSONApi().signUp(signUpModel).enqueue(new Callback<Void>() {

                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                NavHostFragment.findNavController(SignUpFragment.this)
                                        .navigate(R.id.action_signUpFragment_to_logInFragment);
                            } else {
                                Log.d("Error", "Signing up was failed. Error code " + response.code());
                                Toast errorToast = Toast.makeText(getActivity().getApplicationContext(), "Signing up was failed. Error code " + response.code(), Toast.LENGTH_SHORT);
                                errorToast.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("Error", "Signing up was failed. Error:" + t.getMessage());
                            Toast errorToast = Toast.makeText(getActivity().getApplicationContext(), "Signing up was failed. Error:" + t.getMessage(), Toast.LENGTH_SHORT);
                            errorToast.show();
                        }
                    });
                }
                //скрыть клавиатуру
                hideSoftInput(view);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSignUpBinding = null;
    }
    private void hideSoftInput(View view){
        if(view!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
