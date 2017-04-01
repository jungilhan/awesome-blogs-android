package org.petabytes.awesomeblogs.settings;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.coordinator.ActivityGraph;

public class SettingsActivity extends AwesomeActivity {

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.settings)
            .coordinator(R.id.container, new SettingsCoordinator(this::finish))
            .build();
    }

    public static Intent intent(@NonNull Context context) {
        return new Intent(context, SettingsActivity.class);
    }
}
