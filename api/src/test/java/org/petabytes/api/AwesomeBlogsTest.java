package org.petabytes.api;

import org.junit.Test;
import org.petabytes.api.model.Feed;

import retrofit2.Response;
import rx.observers.TestSubscriber;

public class AwesomeBlogsTest {

    @Test
    public void feeds() throws Exception {
        TestSubscriber<Response<Feed>> devSubscriber = new TestSubscriber<>();
        new AwesomeBlogs(true).api().feeds("dev")
            .subscribe(devSubscriber);
        devSubscriber.assertCompleted();

        TestSubscriber<Response<Feed>> companySubscriber = new TestSubscriber<>();
        new AwesomeBlogs(true).api().feeds("company")
            .subscribe(companySubscriber);
        companySubscriber.assertCompleted();

        TestSubscriber<Response<Feed>> insightfulSubscriber = new TestSubscriber<>();
        new AwesomeBlogs(true).api().feeds("insightful")
            .subscribe(insightfulSubscriber);
        insightfulSubscriber.assertCompleted();

        TestSubscriber<Response<Feed>> allSubscriber = new TestSubscriber<>();
        new AwesomeBlogs(true).api().feeds("all")
            .subscribe(allSubscriber);
        allSubscriber.assertCompleted();
    }
}