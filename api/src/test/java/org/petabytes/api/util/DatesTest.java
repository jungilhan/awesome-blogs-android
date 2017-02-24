package org.petabytes.api.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class DatesTest {

    @Test
    public void getRelativeTimeString() throws Exception {
        assertEquals("1 second ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(1)));
        assertEquals("1 minute ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1)));
        assertEquals("1 hour ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)));
        assertEquals("1 day ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));

        assertEquals("30 seconds ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(30)));
        assertEquals("20 minutes ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(20)));
        assertEquals("4 hours ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(4)));
        assertEquals("2 days ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)));
        assertEquals("35 days ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(35)));
    }

    @Test
    public void getDefaultDateFormats() throws ParseException {
        assertEquals("30 seconds ago", Dates.getRelativeTimeString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
            .parseDateTime(new DateTime(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(30)).toString("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"))
            .getMillis()));
    }
}