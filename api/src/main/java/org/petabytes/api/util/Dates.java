package org.petabytes.api.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class Dates {

    public static String getRelativeTimeString(long millis) {
        long now = System.currentTimeMillis();
        if (now - millis < TimeUnit.MINUTES.toMillis(1)) {
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now - millis);
            return seconds == 1 ? seconds + " second ago" : seconds + " seconds ago";
        } else if (now - millis < TimeUnit.HOURS.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now - millis);
            return minutes == 1 ? minutes + " minute ago" : minutes + " minutes ago";
        } else if (now - millis < TimeUnit.DAYS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(now - millis);
            return hours == 1 ? hours + " hour ago" : hours + " hours ago";
        } else {
            long days = TimeUnit.MILLISECONDS.toDays(now - millis);
            return days == 1 ? days + " day ago" : days + " days ago";
        }
    }

    public static DateTimeFormatter getDefaultDateFormats() {
        return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
            .withLocale(Locale.getDefault());
    }
}
