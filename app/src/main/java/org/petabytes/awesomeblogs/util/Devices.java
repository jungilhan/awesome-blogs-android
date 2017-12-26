package org.petabytes.awesomeblogs.util;

import java.util.UUID;

import hugo.weaving.DebugLog;

public class Devices {

    @DebugLog
    public static String getId() {
        return UUID.randomUUID().toString();
    }
}
