package ru.netcracker.bikepacker.service;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;

public class ZipFileManager {
    private final Context context;
    private final UserAccountManager userAccountManager;

    public ZipFileManager(Context context) {
        super();
        this.context = context;
        this.userAccountManager = UserAccountManager.getInstance(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadFile(String filePath) {
        File file = FileUtils.getFile(String.valueOf(filePath));

        MediaType mediaType = null;
        try {
            mediaType = MediaType.parse(Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert mediaType != null;
        RequestBody requestFile = RequestBody.create(
                file,
                mediaType
        );

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<ResponseBody> call = RetrofitManager
                .getInstance(context)
                .getJSONApi()
                .postZipFile(
                        userAccountManager.getCookie(),
                        userAccountManager.getUser().getId(),
                        body
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.v("Upload", "success");

                }
                else {
                    Log.e("Zip Uploading Error", String.format("Error response: %d %s", response.code(), response.message()));
                    ResponseBody errorBody = response.errorBody();
                    if (errorBody != null) {
                        try {
                            Log.e("Zip Uploading Error", "Error body:\n" + errorBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,
                                  @NonNull Throwable t) {
                Log.e("Upload error:", " ", t);
            }
        });
    }

    public void openUploadFileDialog(Context ctx) {
        FileDialogOpener FolderChooseDialog = new FileDialogOpener(ctx, "FileOpen",
                new FileDialogOpener.FileDialogOpenerListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onChosenDir(String chosenDir, String chosenFilename) {
                        try {
                            if (chosenDir != null && chosenFilename != null) {
                                String fullPath = chosenDir + "/" + chosenFilename;
                                Log.v("Filename", fullPath);
                                uploadFile(fullPath);
                            } else {
                                throw new FileNotFoundException("File uploading was failed. No such file or directory.");
                            }
                        } catch (Exception e) {
                            Toast.makeText(ctx, "File uploading was failed!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

        FolderChooseDialog.chooseFile_or_Dir();
    }
}
