package org.petabytes.awesomeblogs.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;

import hugo.weaving.DebugLog;

public class InstanceIdService extends FirebaseInstanceIdService {

    @DebugLog
    @Override
    public void onTokenRefresh() {
        AwesomeBlogsApp.get().preferences()
            .getString("fcm_token").set(FirebaseInstanceId.getInstance().getToken());
    }
}
