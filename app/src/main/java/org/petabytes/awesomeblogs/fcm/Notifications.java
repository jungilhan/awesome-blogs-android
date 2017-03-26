package org.petabytes.awesomeblogs.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.feeds.FeedsActivity;

public class Notifications {

    public static void send(@NonNull Context context, @NonNull String title, @NonNull String message, @NonNull Intent intent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(title)
            .setContentText(message)
            .setTicker(message)
            .setAutoCancel(true)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
            .setWhen(System.currentTimeMillis())
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(System.identityHashCode(message), notificationBuilder.build());
    }
}
