package org.petabytes.awesomeblogs.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Views {

    public static boolean isVisible(@NonNull View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    public static void setVisibleOrGone(@NonNull View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void setVisible(@NonNull View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setGone(@NonNull View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setInvisible(@NonNull View... views) {
        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void showSoftInput(@NonNull View view) {
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideSoftInput(@NonNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getRootView().getWindowToken(), 0);
    }
}
