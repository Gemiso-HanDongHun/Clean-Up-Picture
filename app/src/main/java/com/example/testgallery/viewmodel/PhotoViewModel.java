package com.example.testgallery.viewmodel;



import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.testgallery.models.Image;

/**
 * Created by avukelic on 25-Jul-18.
 */
public class PhotoViewModel extends ViewModel {

    private MutableLiveData<Image> image;

    public LiveData<Image> setImage(Image image) {
        if (this.image == null) {
            this.image = new MutableLiveData<>();
            this.image.setValue(image);
            this.image.getValue().getDominantColor();
        }
        return this.image;
    }

    public void updateImage(Image image) {
        this.image.setValue(image);
        this.image.getValue().getDominantColor();
    }

    public boolean isImageTaken(){
        return image != null;
    }

    public MutableLiveData<Image> getImage() {
        return image;
    }
}
