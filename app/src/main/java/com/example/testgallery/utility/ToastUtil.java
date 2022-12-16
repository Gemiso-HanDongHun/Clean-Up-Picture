package com.example.testgallery.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by avukelic on 26-Jul-18.
 */
public class ToastUtil {

    public static void callShortToast(Context context, int messageResource){
        Toast.makeText(context,messageResource,Toast.LENGTH_SHORT).show();
    }
    public static void callLongToast(Context context, int messageResource){
        Toast.makeText(context,messageResource,Toast.LENGTH_LONG).show();
    }
}
