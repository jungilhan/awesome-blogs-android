package org.petabytes.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import org.petabytes.api.source.local.AwesomeBlogsLocalSource;
import org.petabytes.api.source.local.Entry;
import org.petabytes.api.source.local.Feed;
import org.petabytes.api.source.remote.AwesomeBlogsRemoteSource;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Api implements DataSource {

    private final AwesomeBlogsLocalSource localSource;
    private final AwesomeBlogsRemoteSource remoteSource;

    @VisibleForTesting
    Api(AwesomeBlogsLocalSource localSource, AwesomeBlogsRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }

    public Api(@NonNull Context context, boolean loggable) {
        localSource = new AwesomeBlogsLocalSource(context);
        remoteSource = new AwesomeBlogsRemoteSource(loggable);
    }

    @Override
    public Observable<Feed> getFeed(@NonNull final String category) {
        return getFeed(category, false);
    }

    public Observable<Feed> getFeed(@NonNull final String category, final boolean forceRefresh) {
        return localSource.getFeed(category)
            .filter(new Func1<Feed, Boolean>() {
                @Override
                public Boolean call(Feed feed) {
                    return !forceRefresh;
                }
            })
            .doOnNext(new Action1<Feed>() {
                @Override
                public void call(Feed feed) {
                    if (!feed.isExpires()) {
                        return;
                    }
                    remoteSource.getFeed(category)
                        .doOnNext(new Action1<Feed>() {
                            @Override
                            public void call(Feed feed) {
                                localSource.filterFreshEntries(feed)
                                    .subscribe();
                            }
                        })
                        .doOnNext(new Action1<Feed>() {
                            @Override
                            public void call(Feed feed) {
                                localSource.saveFeed(feed);
                            }
                        })
                        .onErrorResumeNext(Observable.<Feed>empty())
                        .subscribeOn(Schedulers.io())
                        .subscribe();
                }
            })
            .switchIfEmpty(Observable.defer(new Func0<Observable<Feed>>() {
                @Override
                public Observable<Feed> call() {
                    return remoteSource.getFeed(category)
                        .doOnNext(new Action1<Feed>() {
                            @Override
                            public void call(Feed feed) {
                                localSource.saveFeed(feed);
                            }
                        });
                }
            }));
    }

    public Observable<Pair<String, List<Entry>>> getFreshEntries() {
        return localSource.getFreshEntries();
    }

    public Observable<Boolean> isRead(@NonNull String link) {
        return localSource.isRead(link);
    }

    public void markAsRead(@NonNull String title, @NonNull String author, @NonNull String updatedAt,
                           @NonNull String summary, @NonNull String link, long readAt) {
        localSource.markAsRead(title, author, updatedAt, summary, link, readAt);
    }
}
