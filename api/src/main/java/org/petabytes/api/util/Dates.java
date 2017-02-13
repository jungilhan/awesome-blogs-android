package org.petabytes.api.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Dates {

    public static String getRelativeTimeString(long millis) {
        long now = System.currentTimeMillis();
        if (now - millis < TimeUnit.MINUTES.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toSeconds(now - millis) + " seconds ago";
        } else if (now - millis < TimeUnit.HOURS.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toMinutes(now - millis) + " minutes ago";
        } else if (now - millis < TimeUnit.DAYS.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toHours(now - millis) + " hours ago";
        } else {
            return TimeUnit.MILLISECONDS.toDays(now - millis) + " days ago";
        }
    }

    public static SimpleDateFormat getDefaultDateFormats() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.getDefault());
    }
}
