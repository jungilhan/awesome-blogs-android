package org.petabytes.api;

import com.annimon.stream.function.Supplier;

import org.junit.Test;
import org.petabytes.api.source.remote.AwesomeBlogsRemoteSource;

public class AwesomeBlogsRemoteSourceTest {

    @Test
    public void feed() throws Exception {
        Supplier<String> emptySupplier = () -> "";
        new AwesomeBlogsRemoteSource(emptySupplier, emptySupplier, emptySupplier, emptySupplier, true)
            .getFeed("dev")
            .test()
            .assertCompleted();

        new AwesomeBlogsRemoteSource(emptySupplier, emptySupplier, emptySupplier, emptySupplier, true)
            .getFeed("company")
            .test()
            .assertCompleted();

        new AwesomeBlogsRemoteSource(emptySupplier, emptySupplier, emptySupplier, emptySupplier, true)
            .getFeed("insightful")
            .test()
            .assertCompleted();

        new AwesomeBlogsRemoteSource(emptySupplier, emptySupplier, emptySupplier, emptySupplier, true)
            .getFeed("all")
            .test()
            .assertCompleted();
    }
}