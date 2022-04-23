package ru.netcracker.bikepacker.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.annotation.RequiresApi;

import android.util.Base64;

public class ImageConverter {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap decode(String encodeStringImage){
        byte[] bytes = Base64.decode(encodeStringImage, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return image;
    }
}
