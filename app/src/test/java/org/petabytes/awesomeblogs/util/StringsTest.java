package org.petabytes.awesomeblogs.util;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringsTest {

    @Test
    public void replaceLast() throws Exception {
        Assert.assertEquals("2017-02-14T13:54:04+0900", Strings.replaceLast("2017-02-14T13:54:04+09:00", ":", ""));
    }

}