package org.petabytes.awesomeblogs.referrer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.annimon.stream.Optional;

import org.petabytes.awesomeblogs.util.Analytics;

public class InstallReferrerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Optional<String> referrer = intent.getExtras() == null
            ? Optional.empty() : Optional.ofNullable(intent.getExtras().getString("referrer", null));
        referrer.ifPresent(r ->
            Analytics.event(Analytics.Event.INSTALL_REFERRER, "referrer", r));
    }
}
