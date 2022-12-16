package com.example.testgallery.utility;

import android.graphics.Bitmap;

import com.example.testgallery.models.Image;

/**
 * Created by avukelic on 26-Jul-18.
 */
public class ImageUtil {

    public static Image mapBitmapToImage(Bitmap bitmap){
        Image image = new Image();
        image.setBitmap(bitmap);
        return image;
    }
}

