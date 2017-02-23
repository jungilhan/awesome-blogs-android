package org.petabytes.awesomeblogs.util;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.format.DateUtils;

import com.tapadoo.alerter.Alerter;

import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.Activity;

public abstract class Alerts {

    public static void show(@NonNull Activity context, @StringRes int titleResId, @StringRes int messageResId) {
        show(context, titleResId, messageResId, (long) (1.7f * DateUtils.SECOND_IN_MILLIS));
    }

    public static void show(@NonNull Activity context, @StringRes int titleResId, @StringRes int messageResId, long milliseconds) {
        Alerter.create(context)
            .setTitle(titleResId)
            .setText(messageResId)
            .setBackgroundColor(R.color.colorAccent)
            .setDuration(milliseconds)
            .show();
    }
}
