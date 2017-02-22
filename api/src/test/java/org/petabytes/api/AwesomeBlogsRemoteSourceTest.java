package org.petabytes.api;

import org.junit.Test;
import org.petabytes.api.source.local.Feed;
import org.petabytes.api.source.remote.AwesomeBlogsRemoteSource;

import rx.functions.Action1;

public class AwesomeBlogsRemoteSourceTest {

    @Test
    public void feeds() throws Exception {
        new AwesomeBlogsRemoteSource(new Action1<Feed>() {
            @Override
            public void call(Feed feed) {}
        }, true).getFeed("dev")
                .test()
                .assertCompleted();

        new AwesomeBlogsRemoteSource(new Action1<Feed>() {
            @Override
            public void call(Feed feed) {}
        }, true).getFeed("company")
                .test()
                .assertCompleted();

        new AwesomeBlogsRemoteSource(new Action1<Feed>() {
            @Override
            public void call(Feed feed) {}
        }, true).getFeed("insightful")
                .test()
                .assertCompleted();

        new AwesomeBlogsRemoteSource(new Action1<Feed>() {
            @Override
            public void call(Feed feed) {}
        }, true).getFeed("all")
                .test()
                .assertCompleted();
    }
}