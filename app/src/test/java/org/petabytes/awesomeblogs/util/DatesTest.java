package org.petabytes.awesomeblogs.util;

import junit.framework.Assert;

import org.junit.Test;

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

}