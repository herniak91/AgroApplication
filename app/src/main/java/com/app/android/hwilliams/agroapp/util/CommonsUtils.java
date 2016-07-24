package com.app.android.hwilliams.agroapp.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Hernan on 7/23/2016.
 */
public class CommonsUtils {

    public static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
