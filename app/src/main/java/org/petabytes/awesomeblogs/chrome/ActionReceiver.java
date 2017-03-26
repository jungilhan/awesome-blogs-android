package org.petabytes.awesomeblogs.chrome;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Intents;
import org.petabytes.awesomeblogs.util.Strings;

import hugo.weaving.DebugLog;

public class ActionReceiver extends BroadcastReceiver {

    public static final String ACTION = "org.petabytes.awesomeblogs.chrome.action";
    public static final int COPY_LINK = 0;
    public static final int SHARE = 1;

    @DebugLog
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        String url = intent.getDataString();
        if (url == null) {
            return;
        }
        switch (intent.getIntExtra(ACTION, -1)) {
            case COPY_LINK:
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(Strings.EMPTY, url);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, R.string.copy_link_completed, Toast.LENGTH_SHORT).show();
                Analytics.event(Analytics.Event.COPY_LINK, Analytics.Param.LINK, url);
                break;
            case SHARE:
                context.startActivity(Intents.createShareIntent(Strings.EMPTY, url));
                Analytics.event(Analytics.Event.SHARE, Analytics.Param.LINK, url);
                break;
        }
    }
}
