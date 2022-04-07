package ru.netcracker.bikepacker.view;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.databinding.FragmentCreatePointBinding;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.PointModel;
import ru.netcracker.bikepacker.tracks.listeners.OnCancelButtonListener;

public class CreatePointFragment extends Fragment {

    private RecordFragment recordFragment;
    private Button buttonCancel;
    private ImageButton left;
    private ImageButton right;
    private Button buttonPoint;
    private EditText descriptionText;
    private OnCancelButtonListener onCancelButtonListener;
    private ActivityResultLauncher activityResultLauncherAddPhoto;
    private ActivityResultLauncher activityResultLauncherTakePhoto;
    private ImageView imageView;
    private UserAccountManager userAccountManager;
    private List<String> imagesDecodedList;

    public void setRecordFragment(RecordFragment recordFragment) {
        this.recordFragment = recordFragment;
    }

    public void setOnCancelButtonListener(OnCancelButtonListener onCancelButtonListener) {
        this.onCancelButtonListener = onCancelButtonListener;
    }

    public CreatePointFragment() {
        this.userAccountManager = UserAccountManager.getInstance(getContext());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonCancel = view.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(view1 -> onCancelButtonListener.onClick());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCreatePointBinding fragmentCreatePointBinding = FragmentCreatePointBinding.inflate(inflater, container, false);
        buttonCancel = fragmentCreatePointBinding.buttonCancel;
        descriptionText = fragmentCreatePointBinding.description;
        Button buttonAccept = fragmentCreatePointBinding.acceptButton;
        ImageButton buttonAddPhoto = fragmentCreatePointBinding.addPhoto;
        imageView = fragmentCreatePointBinding.addPhotoView;
        left = fragmentCreatePointBinding.left;
        right = fragmentCreatePointBinding.right;
        ImageButton buttonTakePhoto = fragmentCreatePointBinding.takePhoto;
        imagesDecodedList = new ArrayList<>();
        activityResultLauncherTakePhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback() {
                    @Override
                    public void onActivityResult(Object r) {
                        ActivityResult result = (ActivityResult) r;
                        if (result.getResultCode() == getActivity().RESULT_OK
                                && null != result.getData()) {
                            Bundle extras = result.getData().getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imagesDecodedList.add(encodeImage(imageBitmap));
                            right.setVisibility(View.INVISIBLE);
                            left.setVisibility(View.INVISIBLE);
                            imageView.setImageBitmap(imageBitmap);
                        }
                    }
                });
        activityResultLauncherAddPhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback() {
                    @Override
                    public void onActivityResult(Object r) {
                        ActivityResult result = (ActivityResult) r;
                        try {
                            if (result.getResultCode() == getActivity().RESULT_OK
                                    && null != result.getData()) {
                                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                                if (result.getData().getData() != null) {
                                    Uri mImageUri = result.getData().getData();
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
                                    imagesDecodedList.add(encodeImage(bitmap));
                                    Cursor cursor = getActivity().getContentResolver().query(mImageUri,
                                            filePathColumn, null, null, null);
                                    cursor.moveToFirst();
                                    cursor.close();
                                } else {
                                    if (result.getData().getClipData() != null) {
                                        ClipData mClipData = result.getData().getClipData();
                                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                                            ClipData.Item item = mClipData.getItemAt(i);
                                            Uri uri = item.getUri();
                                            Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                                            cursor.moveToFirst();
                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                            String imageStr = encodeImage(bitmap);
                                            imagesDecodedList.add(imageStr);
                                            cursor.close();
                                        }
                                        Log.v("LOG_TAG", "Selected Images" + imagesDecodedList.size());
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "You haven't picked Image",
                                        Toast.LENGTH_LONG).show();
                            }

                            if (imagesDecodedList != null && imagesDecodedList.size() > 0) {
                                imageView.setImageBitmap(decodeImage(imagesDecodedList.get(0)));
                                final String[] currIm = {imagesDecodedList.get(0)};
                                int i = 0;
                                if (imagesDecodedList.size() > 1) {
                                    right.setVisibility(View.VISIBLE);
                                }
                                right.setOnClickListener(view -> {
                                    if (imagesDecodedList.indexOf(currIm[0]) < imagesDecodedList.size() - 1) {
                                        left.setVisibility(View.VISIBLE);
                                        String currIm2 = imagesDecodedList.get(imagesDecodedList.indexOf(currIm[0]) + 1);
                                        imageView.setImageBitmap(decodeImage(currIm2));
                                        currIm[0] = currIm2;
                                        if (imagesDecodedList.indexOf(currIm[0]) == imagesDecodedList.size() - 1) {
                                            right.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                                left.setOnClickListener(view -> {
                                    if (imagesDecodedList.indexOf(currIm[0]) > 0) {
                                        right.setVisibility(View.VISIBLE);
                                        String currIm2 = imagesDecodedList.get(imagesDecodedList.indexOf(currIm[0]) - 1);
                                        imageView.setImageBitmap(decodeImage(currIm2));
                                        currIm[0] = currIm2;
                                        if ((imagesDecodedList.indexOf(currIm[0]) == 0)) {
                                            left.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                });
                            }
                        } catch (
                                Exception e) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
        );

        buttonAccept.setOnClickListener(view -> {
            Optional descriptionOpt = Optional.ofNullable(descriptionText.getText());
            String description = descriptionOpt.orElse("").toString().trim();
            long track_id = recordFragment.getTrackRecorder().getTrackId();
            PointModel pointModel = new PointModel(description, track_id, recordFragment.getTrackRecorder().getLastLocation().getLatitude(),
                    recordFragment.getTrackRecorder().getLastLocation().getLongitude(), imagesDecodedList);
            recordFragment.getTrackRecorder().addPoint(description);
            sendData(pointModel);
            FragmentManager f = getActivity().getSupportFragmentManager();
            buttonPoint.setVisibility(View.VISIBLE);
            descriptionText.setText("");
            f.beginTransaction()
                    .remove(Objects.requireNonNull(f.findFragmentByTag("point")))
                    .show(Objects.requireNonNull(f.findFragmentByTag("record")))
                    .commit();
        });

        buttonAddPhoto.setOnClickListener(view -> {
            imagesDecodedList = new ArrayList<>();
            Intent cameraIntent = new Intent();
            cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
            cameraIntent.setType("image/*");
            Intent.createChooser(cameraIntent, "Select Picture");
            try {
                activityResultLauncherAddPhoto.launch(cameraIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "(R.string.error)", Toast.LENGTH_SHORT).show();
            }
        });

        buttonTakePhoto.setOnClickListener(view -> {
            imagesDecodedList = new ArrayList<>();
            Intent takePictureIntent = new Intent();
            takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                activityResultLauncherTakePhoto.launch(takePictureIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "(R.string.error)", Toast.LENGTH_SHORT).show();
            }
        });
        return fragmentCreatePointBinding.getRoot();
    }

    private Bitmap decodeImage(String strIm) {
        byte[] bytes1 = Base64.decode(strIm, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes1, 0, bytes1.length);
    }

    private String encodeImage(Bitmap bitmapImage) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] data = bos.toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public void setButtonPoint(Button buttonPoint) {
        this.buttonPoint = buttonPoint;
    }

    public void sendData(PointModel pointModel) {
        RetrofitManager.getInstance(getContext())
                .getJSONApi()
                .addPoint(userAccountManager.getCookie(), pointModel)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        Log.d("Point sending callback", "SENDED");
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(t.getMessage(), "Ошибка добавления точки");
                    }
                });
    }
}