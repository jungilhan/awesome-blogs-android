package org.petabytes.awesomeblogs.chrome;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

import org.petabytes.awesomeblogs.R;

public class Chromes {

    public static void open(@NonNull Context context, @NonNull String url) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.white));
        builder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.back_black));
        builder.addMenuItem(context.getString(R.string.copy_link), createPendingIntent(context, ActionReceiver.COPY_LINK));
        builder.addMenuItem(context.getString(R.string.share), createPendingIntent(context, ActionReceiver.SHARE));
        CustomTabsIntent intent = builder.build();
        intent.launchUrl(context, uri);
    }

    private static PendingIntent createPendingIntent(@NonNull Context context, int action) {
        Intent intent = new Intent(context, ActionReceiver.class);
        intent.putExtra(ActionReceiver.ACTION, action);
        return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
