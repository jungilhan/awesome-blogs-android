package org.petabytes.awesomeblogs.util;

import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;

public final class Preferences {

    private static final String CATEGORY = "category";
    private static final String MORNING_DIGEST_AT = "digest_at";
    private static final String EVENING_DIGEST_AT = "evening_digest_at";
    private static final String DEVICE_ID = "device_id";
    private static final String FCM_TOKEN = "fcm_token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String SETTINGS_DIGEST = "settings_digest";
    private static final String SETTINGS_SILENT = "settings_silent";

    private Preferences() {
    }

    public static Preference<String> category() {
        return AwesomeBlogsApp.get().preferences().getString(CATEGORY, "all");
    }

    public static Preference<Long> morningDigestAt() {
        return AwesomeBlogsApp.get().preferences().getLong(MORNING_DIGEST_AT, 0L);
    }

    public static Preference<Long> eveningDigestAt() {
        return AwesomeBlogsApp.get().preferences().getLong(EVENING_DIGEST_AT, 0L);
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

    public static Preference<Boolean> digest() {
        return AwesomeBlogsApp.get().preferences().getBoolean(SETTINGS_DIGEST, Boolean.TRUE);
    }

    public static Preference<Boolean> silent() {
        return AwesomeBlogsApp.get().preferences().getBoolean(SETTINGS_SILENT, Boolean.FALSE);
    }
}
