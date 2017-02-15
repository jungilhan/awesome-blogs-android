package org.petabytes.api;

import org.junit.Test;
import org.petabytes.api.source.local.Feed;
import org.petabytes.api.source.remote.AwesomeBlogsRemoteSource;

import rx.functions.Action1;
import rx.observers.TestSubscriber;

public class AwesomeBlogsRemoteSourceTest {

    @Test
    public void feeds() throws Exception {
        TestSubscriber<Feed> devSubscriber = new TestSubscriber<>();
        new AwesomeBlogsRemoteSource(new Action1<Feed>() {
            @Override
            public void call(Feed feed) {}
        }, true).getFeed("dev").subscribe(devSubscriber);
        devSubscriber.assertCompleted();

        TestSubscriber<Feed> companySubscriber = new TestSubscriber<>();
        new AwesomeBlogsRemoteSource(new Action1<Feed>() {
            @Override
            public void call(Feed feed) {}
        }, true).getFeed("company").subscribe(companySubscriber);
        companySubscriber.assertCompleted();

        TestSubscriber<Feed> insightfulSubscriber = new TestSubscriber<>();
        new AwesomeBlogsRemoteSource(new Action1<Feed>() {
            @Override
            public void call(Feed feed) {}
        }, true).getFeed("insightful").subscribe(insightfulSubscriber);
        insightfulSubscriber.assertCompleted();

        TestSubscriber<Feed> allSubscriber = new TestSubscriber<>();
        new AwesomeBlogsRemoteSource(new Action1<Feed>() {
            @Override
            public void call(Feed feed) {}
        }, true).getFeed("all").subscribe(allSubscriber);
        allSubscriber.assertCompleted();
    }
}