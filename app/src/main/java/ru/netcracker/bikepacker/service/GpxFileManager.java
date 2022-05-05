package ru.netcracker.bikepacker.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.TrackModel;

public class GpxFileManager {

    private final String DEFAULT_EXPORTING_GPX_TITLE = "bikepacker-gpx-track" + ".gpx";

    private final Context context;
    private Writer writer;
    private String absolutePath;
    private final UserAccountManager userAccountManager;

    public GpxFileManager(Context context) {
        super();
        this.context = context;
        this.userAccountManager = UserAccountManager.getInstance(context);
    }

    private static String getStringFromFile(String filePath) {
        String stringFromFile = null;

        if (filePath != null) {
            File file = new File(filePath);

            if (file.exists() && filePath != null) {
                try (FileInputStream fin = new FileInputStream(file)) {
                    stringFromFile = IOUtils.toString(fin, StandardCharsets.UTF_8);
                    fin.close();
                    return stringFromFile;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return stringFromFile;
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        reader.close();
        return sb.toString();
    }

    public void importGpx(Context ctx) {
        FileDialogOpener FolderChooseDialog = new FileDialogOpener(ctx, "FileOpen",
                new FileDialogOpener.FileDialogOpenerListener() {
                    @Override
                    public void onChosenDir(String chosenDir, String chosenFilename) {
                        try {
                            String importedGPX = importGpxFromFile(ctx, chosenDir, chosenFilename);
                            TrackModel importedTrack = new TrackModel();
                            importedTrack.setUser(userAccountManager.getUser());
                            importedTrack.setGpx(importedGPX);
                            importedTrack.setTrackComplexity(0);
                            importedTrack.setTravelTime(0);
                            RetrofitManager.getInstance(ctx).getJSONApi().postTrack(userAccountManager.getCookie(), importedTrack).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(ctx, "GPX file was imported successfully!", Toast.LENGTH_SHORT).show();
                                        Log.d("GPX importing", "File was imported successfully");
                                    } else {
                                        Toast.makeText(ctx, "GPX file importing was failed!", Toast.LENGTH_SHORT).show();
                                        Log.e("GPX importing", String.format("Importing error response: %d %s", response.code(), response.message()));
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                    Log.e("Track sending callback", "error send response. Error message: " + t.getMessage(), t);
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(ctx, "GPX file importing was failed!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

        FolderChooseDialog.chooseFile_or_Dir();
    }

    public void exportGpx(Context ctx, String exportingGpx) {
        FileDialogOpener FileOpenDialog = new FileDialogOpener(ctx, "FileSave",
                new FileDialogOpener.FileDialogOpenerListener() {
                    @Override
                    public void onChosenDir(String chosenDir, String chosenFilename) {
                        try {
                            exportGpxToFile(ctx, chosenDir, chosenFilename, exportingGpx);
                            Toast.makeText(ctx, "GPX file was exported successfully!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(ctx, "GPX file exporting was failed!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

        FileOpenDialog.Default_File_Name = DEFAULT_EXPORTING_GPX_TITLE;
        FileOpenDialog.chooseFile_or_Dir();
    }

    private void exportGpxToFile(Context context, String chosenDir, String fileName, String gpxString) throws Exception {
        if (chosenDir != null && fileName != null && gpxString != null) {
            String fullPath = chosenDir + "/" + fileName;

            try (FileWriter writer = new FileWriter(fullPath)) {
                writer.write(gpxString);
                Log.w("Export GPX", "GPX was exported. File " + fullPath + " was successfully saved.");
                Toast.makeText(context, "Your track was successfully exported into " + fullPath,
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.w("Export GPX", e.getMessage(), e);
                Toast.makeText(context, "Unable to write file to external storage. Check app permissions.",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            throw new Exception("Exporting GPX was failed. No such file or directory or GPX string is null.");
        }
    }

    private String importGpxFromFile(Context context, String chosenDir, String fileName) throws Exception {
        if (chosenDir != null && fileName != null) {
            String fullPath = chosenDir + "/" + fileName;
            return getStringFromFile(fullPath);
        } else {
            throw new FileNotFoundException("Importing GPX was failed. No such file or directory.");
        }
    }

    private Writer getWriter() {
        return writer;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
