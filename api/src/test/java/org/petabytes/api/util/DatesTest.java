package org.petabytes.api.util;

import junit.framework.Assert;

import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DatesTest {

    @Test
    public void getRelativeTimeString() throws Exception {
        Assert.assertEquals("30 seconds ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(30)));
        Assert.assertEquals("20 minutes ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(20)));
        Assert.assertEquals("4 hours ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(4)));
        Assert.assertEquals("1 days ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
        Assert.assertEquals("35 days ago", Dates.getRelativeTimeString(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(35)));
    }

    @Test
    public void getDefaultDateFormats() throws ParseException {
        Assert.assertEquals("30 seconds ago", Dates.getRelativeTimeString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
            .parseDateTime("2017-02-18T13:40:00.000+09:00")
            .getMillis()));
    }
}