package org.petabytes.awesomeblogs.migration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.BuildConfig;
import org.petabytes.awesomeblogs.util.Preferences;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class MigrationReceiver extends BroadcastReceiver {

    @DebugLog
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (BuildConfig.VERSION_CODE >= 13) {
            if (!Preferences.silent().get()) {
                Preferences.silent().set(true);
            }
        }
    }
}
