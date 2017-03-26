package org.petabytes.awesomeblogs.digest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import hugo.weaving.DebugLog;

public class StartUpReceiver extends BroadcastReceiver {

    @DebugLog
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Schedulers.set(context);
    }
}
