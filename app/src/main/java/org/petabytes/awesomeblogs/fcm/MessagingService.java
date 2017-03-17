package org.petabytes.awesomeblogs.fcm;

import android.support.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
            Notifications.send(this, remoteMessage.getNotification().getBody());
        }
    }
}
