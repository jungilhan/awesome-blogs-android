package org.petabytes.awesomeblogs.referrer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Strings;

public class InstallReferrerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        String referrer = intent.getExtras() == null
            ? Strings.EMPTY : intent.getExtras().getString("referrer", Strings.EMPTY);
        Analytics.event(Analytics.Event.INSTALL_REFERRER, "referrer", referrer);
    }
}
