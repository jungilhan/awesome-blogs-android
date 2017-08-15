package org.petabytes.awesomeblogs.util;

import android.support.annotation.NonNull;
import android.util.Base64;

import hugo.weaving.DebugLog;

public class Xors {

    @DebugLog
    public static String encode(@NonNull String input, @NonNull String key) {
        return new String(Base64.encode(xorWithKey(input.getBytes(), key.getBytes()), Base64.DEFAULT));
    }

    @DebugLog
    public static String decode(@NonNull String input, @NonNull String key) {
        return new String(xorWithKey(Base64.decode(input.getBytes(), Base64.DEFAULT), key.getBytes()));
    }

    private static byte[] xorWithKey(@NonNull byte[] input, @NonNull byte[] key) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ key[i%key.length]);
        }
        return output;
    }
}
