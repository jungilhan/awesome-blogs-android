package org.petabytes.awesomeblogs;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;

import com.squareup.coordinators.Coordinators;

import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.ActivityLayoutBinder;

import butterknife.BindView;
import butterknife.ButterKnife;

class DebugActivityLayoutBinder implements ActivityLayoutBinder {

    @Override
    public ViewGroup bind(@NonNull Activity activity) {
        activity.setContentView(R.layout.activity);
        Coordinators.bind(activity.findViewById(R.id.debug), $ ->
            new DebugCoordinator(activity, (DrawerLayout) activity.findViewById(R.id.debug_drawer)));
        return (ViewGroup) activity.findViewById(R.id.activity);
    }
}
