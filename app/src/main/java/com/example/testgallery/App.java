package com.example.testgallery;

import android.app.Application;
import android.content.Context;

/**
 * Created by avukelic on 25-Jul-18.
 */
public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
