package org.petabytes.awesomeblogs.util;

import android.support.annotation.NonNull;
import android.view.View;

public class Views {

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
}
