package org.petabytes.api;

import android.content.Context;
import android.support.annotation.NonNull;

import org.petabytes.api.source.local.AwesomeBlogsLocalSource;
import org.petabytes.api.source.local.Feed;
import org.petabytes.api.source.remote.AwesomeBlogsRemoteSource;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;

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
            .switchIfEmpty(Observable.defer(new Func0<Observable<Feed>>() {
                @Override
                public Observable<Feed> call() {
                    return remoteSource.getFeed(category);
                }
            }));
    }

    public Observable<Boolean> isRead(@NonNull String link) {
        return localSource.isRead(link);
    }

    public void markAsRead(@NonNull String title, @NonNull String author, @NonNull String updatedAt,
                           @NonNull String summary, @NonNull String link, long readAt) {
        localSource.markAsRead(title, author, updatedAt, summary, link, readAt);
    }
}
