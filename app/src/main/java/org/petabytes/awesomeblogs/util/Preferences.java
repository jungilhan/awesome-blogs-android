package org.petabytes.awesomeblogs.util;

import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;

public final class Preferences {

    private static final String CATEGORY = "category";
    private static final String DIGEST_AT = "digest_at";
    private static final String DEVICE_ID = "device_id";
    private static final String FCM_TOKEN = "fcm_token";
    private static final String ACCESS_TOKEN = "access_token";

    private Preferences() {
    }

    public static Preference<String> category() {
        return AwesomeBlogsApp.get().preferences().getString(CATEGORY, "all");
    }

    public static Preference<Long> digestAt() {
        return AwesomeBlogsApp.get().preferences().getLong(DIGEST_AT, 0L);
    }

    public static Preference<String> deviceId() {
        return AwesomeBlogsApp.get().preferences().getString(DEVICE_ID, Strings.EMPTY);
    }

    public static Preference<String> fcmToken() {
        return AwesomeBlogsApp.get().preferences().getString(FCM_TOKEN, Strings.EMPTY);
    }

    public static Preference<String> accessToken() {
        return AwesomeBlogsApp.get().preferences().getString(ACCESS_TOKEN, Strings.EMPTY);
    }
}
