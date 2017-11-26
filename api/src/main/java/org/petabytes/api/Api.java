package org.petabytes.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Supplier;

import org.petabytes.api.source.local.AwesomeBlogsLocalSource;
import org.petabytes.api.source.local.Entry;
import org.petabytes.api.source.local.Favorite;
import org.petabytes.api.source.local.Feed;
import org.petabytes.api.source.local.Read;
import org.petabytes.api.source.remote.AwesomeBlogsRemoteSource;

import java.util.Date;
import java.util.List;

import io.realm.RealmResults;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class Api implements DataSource {

    private final AwesomeBlogsLocalSource localSource;
    private final AwesomeBlogsRemoteSource remoteSource;
    private final PublishSubject<Pair<String, Boolean>> silentRefreshSubject;

    @VisibleForTesting
    Api(@NonNull AwesomeBlogsLocalSource localSource, @NonNull AwesomeBlogsRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
        this.silentRefreshSubject = PublishSubject.create();
    }

    public Api(@NonNull Context context,
               @NonNull Supplier<String> userAgentSupplier, @NonNull Supplier<String> deviceIdSupplier,
               @NonNull Supplier<String> fcmTokenSupplier, @NonNull Supplier<String> accessTokenSupplier, boolean loggable) {
        localSource = new AwesomeBlogsLocalSource(context);
        remoteSource = new AwesomeBlogsRemoteSource(userAgentSupplier, deviceIdSupplier, fcmTokenSupplier, accessTokenSupplier, loggable);
        silentRefreshSubject = PublishSubject.create();
    }

    @Override
    public Observable<Feed> getFeed(@NonNull final String category) {
        return getFeed(category, false);
    }

    public Observable<Feed> getFeed(@NonNull final String category, final boolean forceRefresh) {
        return localSource.getFeed(category)
            .filter(feed -> !forceRefresh)
            .doOnNext(cachedFeed -> {
                if (!cachedFeed.isExpires()) {
                    return;
                }
                remoteSource.getFeed(category)
                    .doOnSubscribe(() -> silentRefreshSubject.onNext(new Pair<>(category, true)))
                    .doOnTerminate(() -> silentRefreshSubject.onNext(new Pair<>(category, false)))
                    .flatMap(freshFeed -> Observable.just(localSource.filterAndDeleteHiddenEntries(freshFeed)))
                    .doOnNext(freshFeed -> localSource.notifyFreshEntries(freshFeed)
                        .subscribe())
                    .map(freshFeed -> localSource.sortByCreatedAt(localSource.fillInCreatedAt(freshFeed, Optional.of(cachedFeed))))
                    .doOnNext(localSource::saveFeed)
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            })
            .switchIfEmpty(Observable.defer(() -> remoteSource.getFeed(category)
                .flatMap(freshFeed -> Observable.just(localSource.filterAndDeleteHiddenEntries(freshFeed)))
                .map(freshFeed -> localSource.sortByCreatedAt(localSource.fillInCreatedAt(freshFeed, Optional.<Feed>empty())))
                .doOnNext(localSource::saveFeed)));
    }

    public Observable<Pair<String, List<Entry>>> getFreshEntries() {
        return localSource.getFreshEntries();
    }

    public Observable<Pair<String, Boolean>> getSilentRefresh() {
        return silentRefreshSubject;
    }

    public Observable<Entry> getEntry(@NonNull String link) {
        return localSource.getEntry(link);
    }

    public Observable<List<Entry>> getEntries(@NonNull String author) {
        return localSource.getEntries(author);
    }

    public Observable<RealmResults<Read>> getHistory() {
        return localSource.getHistory();
    }

    public Observable<RealmResults<Entry>> search(@NonNull String keyword) {
        return localSource.search(keyword);
    }

    public Observable<Boolean> isRead(@NonNull String link) {
        return localSource.isRead(link);
    }

    public void markAsRead(@NonNull Entry entry, long readAt) {
        localSource.markAsRead(entry, readAt);
        if (!BuildConfig.DEBUG) {
            remoteSource.markAsRead(entry);
        }
    }

    public Observable<RealmResults<Favorite>> getFavorites() {
        return localSource.getFavorites();
    }

    public Observable<Boolean> isFavorite(@NonNull String link) {
        return localSource.isFavorite(link);
    }

    public void markAsFavorite(@NonNull Entry entry, long favoriteAt) {
        localSource.markAsFavorite(entry, favoriteAt);
    }

    public void unMarkAsFavorite(@NonNull Entry entry) {
        localSource.unMarkAsFavorite(entry);
    }

    public Observable<Date> getExpiryDate(@NonNull String category) {
        return localSource.getExpiryDate(category);
    }

    public void clearExpiryDate(@NonNull String category) {
        localSource.clearExpiryDate(category);
    }
}
