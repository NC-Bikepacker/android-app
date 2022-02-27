package ru.netcracker.bikepacker.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import java.io.InvalidObjectException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.databinding.FragmentLogInBinding;
import ru.netcracker.bikepacker.manager.SessionManager;
import ru.netcracker.bikepacker.model.AuthModel;
import ru.netcracker.bikepacker.model.UserModel;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.service.EmailValidationService;

public class LogInFragment extends Fragment {

    private @NonNull
    FragmentLogInBinding fragmentLogInBinding;

    private EditText emailField;
    private EditText passwordField;
    private Button submitFormButton;
    private TextView signUpLink;

    private Context context;

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
                NavHostFragment.findNavController(LogInFragment.this)
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

                    if (emailIsValid = EmailValidationService.isEmailValid(email)) {
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
                    authRequest(context, new AuthModel(email, password));
                }
            }
        });
    }

    public void authRequest(Context context, AuthModel authModel) {
        RetrofitManager.getInstance(context).getJSONApi().login(authModel).enqueue(new Callback<UserModel>() {

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                Log.e("Error. Authenticated was failed. ", t.getMessage());
                Toast errorToast = Toast.makeText(context, "Error. Authenticated was failed.", Toast.LENGTH_LONG);
                errorToast.show();
            }

            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    Log.d("Message", "Successfully authenticated. Response " + response.code());
                    String sessionId = response.headers().get("Set-Cookie").split("; ")[0].replace("JSESSIONID=", "");
                    SessionManager sessionManager = SessionManager.getInstance(context);
                    sessionManager.setSessionId(sessionId);

                    if (null != response.body()) {
                        sessionManager.setSessionUser(response.body());
                        NavHostFragment.findNavController(LogInFragment.this)
                                .navigate(R.id.action_logInFragment_to_mainNavigationActivity);
                    } else {
                        try {
                            throw new InvalidObjectException("Log in response body is empty");
                        } catch (InvalidObjectException e) {
                            Log.d("Error", "Log in response body is empty");
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d("Message", "Error. Authenticated was failed. Response " + response.code());
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
