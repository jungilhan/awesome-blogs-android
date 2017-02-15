package org.petabytes.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.petabytes.api.source.local.AwesomeBlogsLocalSource;
import org.petabytes.api.source.local.Feed;
import org.petabytes.api.source.remote.AwesomeBlogsRemoteSource;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class Api implements DataSource {

    private final AwesomeBlogsLocalSource localSource;
    private final AwesomeBlogsRemoteSource remoteSource;

    public Api(@NonNull Context context, boolean loggable) {
        localSource = new AwesomeBlogsLocalSource(context);
        remoteSource = new AwesomeBlogsRemoteSource(new Action1<Feed>() {
            @Override
            public void call(@NonNull Feed feed) {
                localSource.saveFeed(feed);
            }
        }, loggable);
    }

    @Override
    public Observable<Feed> getFeed(@NonNull final String category) {
        return localSource.getFeed(category)
            .flatMap(new Func1<Feed, Observable<Feed>>() {
                @Override
                public Observable<Feed> call(@Nullable Feed feed) {
                    return feed != null ? Observable.just(feed)
                        .doOnTerminate(new Action0() {
                            @Override
                            public void call() {
                                remoteSource.getFeed(category)
                                    .onErrorResumeNext(Observable.<Feed>empty())
                                    .subscribe();
                            }
                        }) : remoteSource.getFeed(category);
                }
            });
    }
}
