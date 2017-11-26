package org.petabytes.api.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Supplier;

import org.petabytes.api.DataSource;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class AwesomeBlogsLocalSource implements DataSource {

    @VisibleForTesting
    final RealmConfiguration config;
    private final BehaviorSubject<Pair<String, List<Entry>>> freshEntriesSubject;

    public AwesomeBlogsLocalSource(@NonNull Context context) {
        Realm.init(context);
        config = createRealmConfiguration();
        freshEntriesSubject = BehaviorSubject.create();
    }

    @Override
    public Observable<Feed> getFeed(@NonNull final String category) {
        return Observable.defer(() -> {
            Realm realm = Realm.getInstance(config);
            try {
                Feed feed = realm.where(Feed.class).equalTo("category", category).findFirst();
                return feed != null ? Observable.just(realm.copyFromRealm(feed)) : Observable.empty();
            } finally {
                realm.close();
            }
        });
    }

    public Observable<Entry> getEntry(@NonNull final String link) {
        return Observable.defer(() -> {
            Realm realm = Realm.getInstance(config);
            try {
                Entry entry = realm.where(Entry.class).equalTo("link", link).findFirst();
                return entry != null ? Observable.just(realm.copyFromRealm(entry)) : Observable.empty();
            } finally {
                realm.close();
            }
        });
    }

    public Observable<List<Entry>> getEntries(@NonNull final String author) {
        return Observable.defer(() -> {
            Realm realm = Realm.getInstance(config);
            try {
                List<Entry> entries = realm.where(Entry.class)
                    .equalTo("author", author).findAllSorted("createdAt", Sort.DESCENDING);
                return Observable.just(realm.copyFromRealm(entries));
            } finally {
                realm.close();
            }
        });
    }

    public Observable<RealmResults<Entry>> search(@NonNull String keyword) {
        final Realm realm = Realm.getInstance(config);
        return realm.where(Entry.class)
            .contains("title", keyword, Case.INSENSITIVE)
            .or()
            .contains("author", keyword, Case.INSENSITIVE)
            .or()
            .contains("link", keyword, Case.INSENSITIVE)
            .findAllSorted("createdAt", Sort.DESCENDING)
            .distinct("link").asObservable()
            .doOnUnsubscribe(realm::close);
    }

    public Feed fillInCreatedAt(@NonNull Feed freshFeed, @NonNull Optional<Feed> cachedFeed) {
        if (cachedFeed.isPresent()) {
            Map<String, Long> createdAtMap = toCreatedAtMap(cachedFeed.get());
            for (int i = freshFeed.getEntries().size() - 1; i >= 0; i--) {
                Entry entry = freshFeed.getEntries().get(i);
                Long createdAt = createdAtMap.get(entry.getLink());
                entry.setCreatedAt(createdAt == null ? System.nanoTime() : createdAt);
            }
        } else {
            for (int i = freshFeed.getEntries().size() - 1; i >= 0; i--) {
                freshFeed.getEntries().get(i).setCreatedAt(System.nanoTime());
            }
        }
        return freshFeed;
    }

    public Feed sortByCreatedAt(@NonNull Feed feed) {
        Collections.sort(feed.getEntries(), (entry1, entry2) ->
            Long.valueOf(entry2.getCreatedAt()).compareTo(entry1.getCreatedAt()));
        return feed;
    }

    public void saveFeed(@NonNull final Feed feed) {
        Realm realm = Realm.getInstance(config);
        realm.executeTransactionAsync(realm1 -> realm1.insertOrUpdate(feed));
        realm.close();
    }

    public Observable<Pair<String, List<Entry>>> getFreshEntries() {
        return freshEntriesSubject;
    }

    public Observable<Pair<String, List<Entry>>> notifyFreshEntries(@NonNull final Feed feed) {
        return getFreshEntries(feed, Collections::emptyList)
            .map(entries -> new Pair<>(feed.getCategory(), entries))
            .doOnNext(freshEntriesSubject::onNext);
    }

    private Observable<List<Entry>> getFreshEntries(@NonNull final Feed feed, @NonNull final Supplier<List<Entry>> ifEmptyExistEntriesSupplier) {
        return getExistEntries(feed)
            .map(existEntries -> existEntries.isEmpty() ? ifEmptyExistEntriesSupplier.get() : Stream.of(feed.getEntries())
            .filter(value -> !existEntries.contains(value))
            .collect(Collectors.toList()));
    }

    private Observable<List<Entry>> getExistEntries(@NonNull final Feed feed) {
        return Observable.fromCallable(() -> {
            Realm realm = Realm.getInstance(config);
            try {
                final RealmResults<Entry> existEntries = realm.where(Entry.class)
                    .in("link", Stream.of(feed.getEntries())
                        .map(Entry::getLink)
                        .toArray(String[]::new))
                    .findAll();
                return realm.copyFromRealm(existEntries);
            } finally {
                realm.close();
            }
        });
    }

    private Map<String, Long> toCreatedAtMap(@NonNull Feed feed) {
        return Stream.of(feed.getEntries())
            .collect(Collectors.toMap(Entry::getLink, Entry::getCreatedAt));
    }

    public Observable<RealmResults<Read>> getHistory() {
        final Realm realm = Realm.getInstance(config);
        return realm.where(Read.class).findAllSorted("readAt", Sort.DESCENDING)
            .asObservable()
            .doOnUnsubscribe(realm::close);
    }

    public Observable<Boolean> isRead(@NonNull final String link) {
        final Realm realm = Realm.getInstance(config);
        return realm.where(Read.class).equalTo("link", link).findAll()
            .asObservable()
            .map(reads -> !reads.isEmpty())
            .doOnUnsubscribe(realm::close);
    }

    public void markAsRead(@NonNull final Entry entry, final long readAt) {
        Realm realm = Realm.getInstance(config);
        realm.executeTransactionAsync(realm1 -> {
            Read read = new Read();
            read.setTitle(entry.getTitle());
            read.setAuthor(entry.getAuthor());
            read.setUpdatedAt(entry.getUpdatedAt());
            read.setSummary(entry.getSummary());
            read.setLink(entry.getLink());
            read.setReadAt(readAt);
            realm1.insertOrUpdate(read);
        });
        realm.close();
    }


    public Observable<RealmResults<Favorite>> getFavorites() {
        final Realm realm = Realm.getInstance(config);
        return realm.where(Favorite.class).findAllSorted("favoriteAt", Sort.DESCENDING)
            .asObservable()
            .doOnUnsubscribe(realm::close);
    }

    public Observable<Boolean> isFavorite(@NonNull final String link) {
        final Realm realm = Realm.getInstance(config);
        return realm.where(Favorite.class).equalTo("link", link).findAll()
            .asObservable()
            .map(reads -> !reads.isEmpty())
            .doOnUnsubscribe(realm::close);
    }

    public void markAsFavorite(@NonNull final Entry entry, final long favoriteAt) {
        Realm realm = Realm.getInstance(config);
        realm.executeTransactionAsync(realm1 -> {
            Favorite read = new Favorite();
            read.setTitle(entry.getTitle());
            read.setAuthor(entry.getAuthor());
            read.setUpdatedAt(entry.getUpdatedAt());
            read.setSummary(entry.getSummary());
            read.setLink(entry.getLink());
            read.setFavoriteAt(favoriteAt);
            realm1.insertOrUpdate(read);
        });
        realm.close();
    }

    public void unMarkAsFavorite(@NonNull final Entry entry) {
        Realm realm = Realm.getInstance(config);
        realm.executeTransactionAsync(realm1 -> realm1.where(Favorite.class)
            .equalTo("link", entry.getLink())
            .findAll()
            .deleteAllFromRealm());
        realm.close();
    }

    public Observable<Date> getExpiryDate(@NonNull String category) {
        final Realm realm = Realm.getInstance(config);
        return realm.where(Feed.class).equalTo("category", category).findAll()
            .asObservable()
            .filter(feeds -> !feeds.isEmpty())
            .map(feeds -> new Date(feeds.get(0).getExpires()))
            .doOnUnsubscribe(realm::close);
    }

    public void clearExpiryDate(@NonNull final String category) {
        Realm realm = Realm.getInstance(config);
        realm.executeTransactionAsync(realm1 -> {
            Feed feed = realm1.where(Feed.class).equalTo("category", category).findFirst();
            if (feed != null) {
                feed.setExpires(0L);
            }
        });
        realm.close();
    }

    public Feed filterAndDeleteHiddenEntries(@NonNull Feed feed) {
        deleteHiddenEntries(feed);
        RealmList<Entry> collect = Stream.of(feed.getEntries())
            .filter(entry -> !entry.isHidden())
            .collect(Collectors.toCollection(RealmList::new));
        feed.setEntries(collect);
        return feed;
    }

    private void deleteHiddenEntries(@NonNull Feed feed) {
        String[] values = Stream.of(feed.getEntries())
            .filter(Entry::isHidden)
            .map(Entry::getLink)
            .toArray(String[]::new);
        if (values.length > 0) {
            deleteEntries(Entry.class, "link", values);
            deleteEntries(Read.class, "link", values);
            deleteEntries(Favorite.class, "link", values);
        }
    }

    private <E extends RealmModel> void deleteEntries(@NonNull final Class<E> className,
                                                      @NonNull final String field,
                                                      @NonNull final String[] values) {
        Realm realm = Realm.getInstance(config);
        realm.executeTransaction(realm1 -> {
            RealmResults<E> hiddenEntries = realm1.where(className).in(field, values).findAll();
            hiddenEntries.deleteAllFromRealm();
        });
        realm.close();
    }

    private RealmConfiguration createRealmConfiguration() {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder()
            .name("awesome_blogs.realm")
            .schemaVersion(3)
            .migration(new Migration());
        return builder.build();
    }
}
