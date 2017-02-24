package org.petabytes.api;

import org.junit.Test;
import org.petabytes.api.source.remote.AwesomeBlogsRemoteSource;

public class AwesomeBlogsRemoteSourceTest {

    @Test
    public void feed() throws Exception {
        new AwesomeBlogsRemoteSource(true)
            .getFeed("dev")
            .test()
            .assertCompleted();

        new AwesomeBlogsRemoteSource(true)
            .getFeed("company")
            .test()
            .assertCompleted();

        new AwesomeBlogsRemoteSource(true)
            .getFeed("insightful")
            .test()
            .assertCompleted();

        new AwesomeBlogsRemoteSource(true)
            .getFeed("all")
            .test()
            .assertCompleted();
    }
}