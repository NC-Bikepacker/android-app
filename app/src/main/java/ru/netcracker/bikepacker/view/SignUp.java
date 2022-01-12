package ru.netcracker.bikepacker.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.apache.commons.lang3.StringUtils;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.databinding.FragmentSignUpBinding;
import ru.netcracker.bikepacker.model.Email;
import ru.netcracker.bikepacker.model.Password;

public class SignUp extends Fragment {

    private @NonNull
    FragmentSignUpBinding fragmentSignUpBinding;

    EditText firstName;
    EditText lastName;
    EditText userName;
    EditText email;
    EditText passwordField;
    EditText confirmPasswordField;
    Button submitFormButton;
    TextView logInLink;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        fragmentSignUpBinding = FragmentSignUpBinding.inflate(inflater, container, false);
        return fragmentSignUpBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstName = fragmentSignUpBinding.layoutSignupFirstnameFieldTextField;
        lastName = fragmentSignUpBinding.layoutSignupLastnameFieldTextField;
        userName = fragmentSignUpBinding.layoutSignupUsernameFieldTextField;
        email = fragmentSignUpBinding.layoutSignupEmailFieldTextField;
        passwordField = fragmentSignUpBinding.layoutSignupPasswordFieldTextField;
        confirmPasswordField = fragmentSignUpBinding.layoutSignupConfirmPasswordFieldTextview;
        submitFormButton = fragmentSignUpBinding.fragmentSignUpButtonSignUp;
        logInLink = fragmentSignUpBinding.layoutSignupSignInLinkTextView;

        logInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SignUp.this)
                        .navigate(R.id.action_signUpFragment_to_logInFragment);
            }
        });

        submitFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = StringUtils.capitalize(firstName.getText().toString().trim().toLowerCase());
                String lastname = StringUtils.capitalize(lastName.getText().toString().trim().toLowerCase());
                String username = userName.getText().toString().trim().toLowerCase();
                String email = firstName.getText().toString().trim();
                String pass = passwordField.getText().toString();
                String passConfirmation = confirmPasswordField.getText().toString();

                boolean fieldsAreNotEmpty = false;
                boolean emailIsValid = false;
                boolean passwordIsValid = false;
                boolean passwordIsConfirmed = false;

                if (firstname.equals("") || lastname.equals("") || username.equals("") || email.equals("") || pass.equals("") || passConfirmation.equals("")) {
                    Toast errorToast = Toast.makeText(getActivity().getApplicationContext(), "All field are required.", Toast.LENGTH_SHORT);
                    errorToast.show();
                } else {
                    fieldsAreNotEmpty = true;

                    if (Email.isEmailValid(email)) {
                        emailIsValid = true;

                        if (pass.length() >= 8 && Password.isValidPassword(pass)) {
                            passwordIsValid = true;

                            if (pass.equals(passConfirmation)) {
                                passwordIsConfirmed = true;

                            } else {
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

                if (fieldsAreNotEmpty && emailIsValid && passwordIsValid && passwordIsConfirmed) {
//                  TODO: Send POST request
                    Toast errorToast = Toast.makeText(getActivity().getApplicationContext(), "Все поля прошли проверку успешно", Toast.LENGTH_SHORT);
                    errorToast.show();

                    NavHostFragment.findNavController(SignUp.this)
                            .navigate(R.id.action_signUpFragment_to_userAccountInfo);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSignUpBinding = null;
    }
}
