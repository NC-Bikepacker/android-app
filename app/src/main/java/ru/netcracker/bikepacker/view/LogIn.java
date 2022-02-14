package ru.netcracker.bikepacker.view;

import android.content.Context;
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

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.controller.AuthRequest;
import ru.netcracker.bikepacker.databinding.FragmentLogInBinding;
import ru.netcracker.bikepacker.manager.SessionManager;
import ru.netcracker.bikepacker.model.AuthModel;
import ru.netcracker.bikepacker.service.EmailValidationService;

public class LogIn extends Fragment {

    private @NonNull
    FragmentLogInBinding fragmentLogInBinding;

    EditText emailField;
    EditText passwordField;
    Button submitFormButton;
    TextView signUpLink;

    Context context;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentLogInBinding = FragmentLogInBinding.inflate(inflater, container, false);
        return fragmentLogInBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();

        emailField = fragmentLogInBinding.layoutSigninEmailFieldTextField;
        passwordField = fragmentLogInBinding.layoutSigninPasswordFieldTextField;
        submitFormButton = fragmentLogInBinding.layoutSigninSubmitBitton;
        signUpLink = fragmentLogInBinding.layoutSigninSignUpLinkTextView;

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LogIn.this)
                        .navigate(R.id.action_logInFragment_to_signUpFragment);
            }
        });

        submitFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString();
                boolean fieldsAreNotEmpty = false;
                boolean passwordIsValid = false;
                boolean emailIsValid = false;

                if (email.equals("") || password.equals("")) {
                    Toast errorToast = Toast.makeText(context, "All field are required.", Toast.LENGTH_SHORT);
                    errorToast.show();
                } else {
                    fieldsAreNotEmpty = true;

                    if (EmailValidationService.isEmailValid(email)) {
                        emailIsValid = true;
//                      TODO: Enable email validation
//                        if (password.length() >= 8 && PasswordGeneratingService.isValidPassword(password)) {
                        if (true) {
                            passwordIsValid = true;

                        } else {
                            Toast errorToast = Toast.makeText(context, "This password is invalid or less then 8 characters.", Toast.LENGTH_SHORT);
                            errorToast.show();
                        }
                    } else {
                        Toast errorToast = Toast.makeText(context, "Email is not valid.", Toast.LENGTH_SHORT);
                        errorToast.show();
                    }
                }

                if (fieldsAreNotEmpty && passwordIsValid && emailIsValid) {

                    AuthModel authModel = new AuthModel(email, password);
                    AuthRequest.authReq(context, authModel);

                    SessionManager sessionManager = new SessionManager(context);

                    System.out.println(sessionManager.getSessionUserEmail());

                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentLogInBinding = null;
    }
}
