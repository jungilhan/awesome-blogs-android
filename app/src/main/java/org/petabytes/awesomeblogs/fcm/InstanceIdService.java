package org.petabytes.awesomeblogs.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.petabytes.awesomeblogs.digest.StartUpReceiver;
import org.petabytes.awesomeblogs.util.Preferences;

import hugo.weaving.DebugLog;

public class InstanceIdService extends FirebaseInstanceIdService {

    @DebugLog
    @Override
    public void onTokenRefresh() {
        Preferences.fcmToken().set(FirebaseInstanceId.getInstance().getToken());
        StartUpReceiver.scheduleAlarm(this);
    }
}
