package org.petabytes.awesomeblogs.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.feeds.FeedsActivity;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class MessagingService extends FirebaseMessagingService {

    @DebugLog
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Timber.d("Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Timber.d("Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(@NonNull String messageBody) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(PendingIntent.getActivity(this, 0, FeedsActivity.intent(this), PendingIntent.FLAG_ONE_SHOT));
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, notificationBuilder.build());
    }
}
