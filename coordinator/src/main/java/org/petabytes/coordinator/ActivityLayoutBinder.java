package org.petabytes.coordinator;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

public interface ActivityLayoutBinder {

    ViewGroup bind(@NonNull Activity activity);

    ActivityLayoutBinder DEFAULT = activity -> {
        activity.setContentView(R.layout.activity);
        return (ViewGroup) activity.findViewById(R.id.activity);
    };
}
