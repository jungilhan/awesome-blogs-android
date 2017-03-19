package org.petabytes.awesomeblogs.util;

import android.content.Context;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import hugo.weaving.DebugLog;

public class Devices {

    @DebugLog
    public static String getId(@NonNull Context context) {
        UUID uuid;
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            if (StringUtils.equals(androidId, "9774d56d682e549c")) {
                String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            } else {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return uuid.toString();
    }
}
