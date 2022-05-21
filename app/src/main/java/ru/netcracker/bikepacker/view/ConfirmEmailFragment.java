package ru.netcracker.bikepacker.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.ImageSliderAdapterToConfirmFragment;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.SessionManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.UserModel;

public class ConfirmEmailFragment extends Fragment {
    Button repeat;
    ImageButton buttonBack;
    SliderView imageSlider;
    View view;
    ImageSliderAdapterToConfirmFragment adapter;
    UserAccountManager userAccountManager;
    AtomicBoolean clickFlag = new AtomicBoolean(true);
    ConstraintLayout logIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_confirm_email, container, false);
        repeat = view.findViewById(R.id.buttonRepeatConfirmEmail);
        imageSlider = view.findViewById(R.id.confirmEmailFragmentImageSlider);
        buttonBack = view.findViewById(R.id.buttonBackConfirmEmailFragment);
        userAccountManager = UserAccountManager.getInstance(getContext());
        logIn = view.findViewById(R.id.loginConstraintButton);

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.image_1);
        images.add(R.drawable.image_2);
        images.add(R.drawable.image_3);

        adapter = new ImageSliderAdapterToConfirmFragment(this.getContext(), images);
        imageSlider.setSliderAdapter(adapter);
        imageSlider.setIndicatorEnabled(false);
        imageSlider.setIndicatorRadius(0);
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        imageSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
        imageSlider.startAutoCycle();

        buttonBack.setOnClickListener(v -> openQuitDialog());

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!clickFlag.get()){
                    Toast toast = Toast.makeText(getContext(), "you can make a request once every 5 seconds", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0 , 160);
                    toast.show();
                    return;
                }
                repeatConfirmEmail();
                new Handler().postDelayed(() -> clickFlag.set(true), 5000);
            }
        });

        logIn.setOnClickListener(v -> checkConfirmEmail());


        return view;
    }
    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                getContext());
        quitDialog.setTitle("Do you want to log out of your account?");
        quitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserAccountManager.getInstance(requireContext()).removeUser();
                SessionManager.getInstance(getContext()).removeSession();
                NavHostFragment.findNavController(ConfirmEmailFragment.this).navigate(R.id.action_confirmEmailFragment_to_logInFragment);
            }
        });

        quitDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }

    private void checkConfirmEmail(){
        RetrofitManager.getInstance(getContext()).getJSONApi()
                .getUserWithID(userAccountManager.getCookie(), userAccountManager.getUser().getId())
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            if(response.body().isAccountVerification()){
                                SessionManager.getInstance(getContext()).getSessionUser().setAccountVerification(true);
                                NavHostFragment.findNavController(ConfirmEmailFragment.this).navigate(R.id.action_confirmEmailFragment_to_mainNavigationActivity);
                            }
                            else {
                                Toast toast = Toast.makeText(getContext(),"Confirm your e-mail address to continue!",Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0 , 160);
                                toast.show();
                            }
                        }
                        else {
                            Toast toast = Toast.makeText(getContext(), "Request error, check internet connection", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0 , 160);
                            toast.show();
                            Log.e("ConfirmEmail", "Ошибка запроса верификации");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                        Toast toast = Toast.makeText(getContext(), "Error when executing the request, contact technical support", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0 , 160);
                        toast.show();
                        Log.e("ConfirmEmail", "Ошибка запроса верификации",t);
                    }
                });
    }

    private void repeatConfirmEmail(){
        clickFlag.set(false);
        RetrofitManager.getInstance(getContext()).getJSONApi()
                .repeatConfirm(userAccountManager.getCookie(), userAccountManager.getUser().getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            try {
                                Toast toast = Toast.makeText(getContext(), response.body().string(),Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0 , 160);
                                toast.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Log.e("ConfirmEmailFragment", "response body error");
                            Toast toast = Toast.makeText(getContext(), "Request error, check internet connection",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0 , 160);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {
                        Log.e("ConfirmEmailFragment", "response error");
                        Toast toast = Toast.makeText(getContext(), "Error when executing the request, contact technical support",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0 , 160);
                        toast.show();
                    }
                });

    }
}