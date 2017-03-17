package org.petabytes.awesomeblogs.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.feeds.FeedsActivity;

public class Notifications {

    public static void send(@NonNull Context context, @NonNull String message) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message)
            .setTicker(message)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(PendingIntent.getActivity(context, 0, FeedsActivity.intent(context, "all"), PendingIntent.FLAG_ONE_SHOT));
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(System.identityHashCode(message), notificationBuilder.build());
    }
}
