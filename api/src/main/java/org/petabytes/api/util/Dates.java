package org.petabytes.api.util;

import android.text.format.DateUtils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;

public class Dates {

    public static String getRelativeTimeString(long millis) {
        final long now = System.currentTimeMillis();
        final long days = TimeUnit.MILLISECONDS.toDays(now - millis);
        if (days < 1) {
            return DateUtils.getRelativeTimeSpanString(millis, now, SECOND_IN_MILLIS).toString();
        } else {
            return days == 1 ? days + " day ago" : days + " days ago";
        }
    }

    public static DateTimeFormatter getDefaultDateFormats() {
        return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
            .withLocale(Locale.getDefault());
    }
}
