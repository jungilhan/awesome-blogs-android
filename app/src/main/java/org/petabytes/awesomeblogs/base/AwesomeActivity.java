package org.petabytes.awesomeblogs.base;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.coordinator.Activity;
import org.petabytes.coordinator.ActivityLayoutBinder;

public abstract class AwesomeActivity extends Activity {

    @Override
    protected ActivityLayoutBinder createActivityLayoutBinder() {
        return AwesomeBlogsApp.get().activityLayoutBinder();
    }

    @Override
    public void onBackPressed() {
        Intent parentActivityIntent = NavUtils.getParentActivityIntent(this);
        if (isTaskRoot() && parentActivityIntent != null) {
            TaskStackBuilder.create(this)
                .addNextIntent(parentActivityIntent)
                .startActivities();
        } else {
            super.onBackPressed();
        }
    }
}
