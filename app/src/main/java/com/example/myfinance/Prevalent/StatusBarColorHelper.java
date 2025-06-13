package com.example.myfinance.Prevalent;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarColorHelper {

    public static void setStatusBarColorFromPrimaryVariant(Activity activity) {

        Window window = activity.getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(com.google.android.material.R.attr.backgroundColor, typedValue, true);
        int colorPrimaryVariant = typedValue.data;

        window.setStatusBarColor(colorPrimaryVariant);
    }
}