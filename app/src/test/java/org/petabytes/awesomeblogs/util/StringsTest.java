package org.petabytes.awesomeblogs.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;


public class StringsTest {

    @Test
    public void replaceLast() throws Exception {
        Assertions.assertThat("2017-02-14T13:54:04+0900")
                .isEqualTo(Strings.replaceLast("2017-02-14T13:54:04+09:00", ":", ""));
    }

}