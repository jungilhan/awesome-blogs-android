package org.petabytes.awesomeblogs.settings;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.coordinator.ActivityGraph;

import hugo.weaving.DebugLog;

public class SettingsActivity extends AwesomeActivity {

    private SettingsCoordinator settingsCoordinator;

    @Override
    protected ActivityGraph createActivityGraph() {
        settingsCoordinator = new SettingsCoordinator(this, this::finish);
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.settings)
            .coordinator(R.id.container, settingsCoordinator)
            .build();
    }

    @DebugLog
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!settingsCoordinator.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static Intent intent(@NonNull Context context) {
        return new Intent(context, SettingsActivity.class);
    }
}
