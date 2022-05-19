package ru.netcracker.bikepacker.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
    ImageButton buttonBack, buttonContinue;
    SliderView imageSlider;
    View view;
    ImageSliderAdapterToConfirmFragment adapter;
    UserAccountManager userAccountManager;
    AtomicBoolean clickFlag = new AtomicBoolean(true);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_confirm_email, container, false);
        repeat = view.findViewById(R.id.buttonRepeatConfirmEmail);
        imageSlider = view.findViewById(R.id.confirmEmailFragmentImageSlider);
        buttonBack = view.findViewById(R.id.buttonBackConfirmEmailFragment);
        buttonContinue = view.findViewById(R.id.buttonContinueConfirmEmailFragment);
        userAccountManager = UserAccountManager.getInstance(getContext());

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
        buttonContinue.setOnClickListener(v -> checkConfirmEmail());

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!clickFlag.get()){
                    Toast toast = Toast.makeText(getContext(), "сделать запрос можно раз в 5 секунд", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0 , 160);
                    toast.show();
                    return;
                }
                repeatConfirmEmail();
                new Handler().postDelayed(() -> clickFlag.set(true), 5000);
            }
        });


        return view;
    }
    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                getContext());
        quitDialog.setTitle("Вы хотите выйти из аккаунта?");
        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserAccountManager.getInstance(requireContext()).removeUser();
                SessionManager.getInstance(getContext()).removeSession();
                NavHostFragment.findNavController(ConfirmEmailFragment.this).navigate(R.id.action_confirmEmailFragment_to_logInFragment);
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
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
                                Toast toast = Toast.makeText(getContext(),"Для того чтобы продолжить, подтвердите почту!",Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0 , 160);
                                toast.show();
                            }
                        }
                        else {
                            Toast toast = Toast.makeText(getContext(), "Ошибка запроса, проверьте соединение интернет", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0 , 160);
                            toast.show();
                            Log.e("ConfirmEmail", "Ошибка запроса верификации");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                        Toast toast = Toast.makeText(getContext(), "Ошибка при совершении запроса, обратитесь в техподдержку", Toast.LENGTH_LONG);
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
                            Toast toast = Toast.makeText(getContext(), "Ошибка запроса, проверьте соединение интернет",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0 , 160);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {
                        Log.e("ConfirmEmailFragment", "response error");
                        Toast toast = Toast.makeText(getContext(), "Ошибка запроса, обратитесь в техподдержку",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0 , 160);
                        toast.show();
                    }
                });

    }
}