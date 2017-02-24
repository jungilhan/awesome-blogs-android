package org.petabytes.api.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.IntFunction;
import com.annimon.stream.function.Predicate;

import org.petabytes.api.DataSource;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class AwesomeBlogsLocalSource implements DataSource {

    @VisibleForTesting
    final RealmConfiguration config;
    private final BehaviorSubject<Pair<String, List<Entry>>> freshEntriesSubject;


    public AwesomeBlogsLocalSource(@NonNull Context context) {
        Realm.init(context);
        config = createRealmConfiguration(context);
        freshEntriesSubject = BehaviorSubject.create();
    }

    @Override
    public Observable<Feed> getFeed(@NonNull final String category) {
        return Observable.defer(new Func0<Observable<Feed>>() {
            @Override
            public Observable<Feed> call() {
                Realm realm = Realm.getInstance(config);
                try {
                    Feed feed = realm.where(Feed.class).equalTo("category", category).findFirst();
                    return feed != null ? Observable.just(realm.copyFromRealm(feed)) : Observable.<Feed>empty();
                } finally {
                    realm.close();
                }
            }
        });
    }

    public void saveFeed(@NonNull final Feed feed) {
        Realm realm = Realm.getInstance(config);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(final Realm realm) {
                realm.insertOrUpdate(feed);
            }
        });
        realm.close();
    }

    public Observable<Pair<String, List<Entry>>> getFreshEntries() {
        return freshEntriesSubject;
    }

    public Observable<Pair<String, List<Entry>>> filterFreshEntries(@NonNull final Feed feed) {
        return Observable.fromCallable(new Callable<Pair<String, List<Entry>>>() {
            public Pair<String, List<Entry>> call() throws Exception {
                Realm realm = Realm.getInstance(config);
                try {
                    final RealmResults<Entry> existEntries = realm.where(Entry.class)
                        .in("link", Stream.of(feed.getEntries())
                            .map(new Function<Entry, String>() {
                                @Override
                                public String apply(Entry entry) {
                                    return entry.getLink();
                                }
                            }).toArray(new IntFunction<String[]>() {
                                @Override
                                public String[] apply(int size) {
                                    return new String[size];
                                }
                            }))
                        .findAll();

                    return existEntries.isEmpty()
                        ? new Pair<>(feed.getCategory(), Collections.<Entry>emptyList())
                        : new Pair<>(feed.getCategory(), Stream.of(feed.getEntries())
                        .filter(new Predicate<Entry>() {
                            @Override
                            public boolean test(Entry value) {
                                return !existEntries.contains(value);
                            }
                        })
                        .collect(Collectors.<Entry>toList()));
                } finally {
                    realm.close();
                }
            }
        }).doOnNext(new Action1<Pair<String, List<Entry>>>() {
            @Override
            public void call(Pair<String, List<Entry>> pair) {
                freshEntriesSubject.onNext(pair);
            }
        });
    }

    public Observable<Boolean> isRead(@NonNull final String link) {
        final Realm realm = Realm.getInstance(config);
        return realm.where(Read.class).equalTo("link", link).findAll().asObservable()
            .map(new Func1<RealmResults<Read>, Boolean>() {
                @Override
                public Boolean call(RealmResults<Read> reads) {
                    return !reads.isEmpty();
                }
            })
            .doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    realm.close();
                }
            });
    }

    public void markAsRead(@NonNull final String title, @NonNull final String author, @NonNull final String updatedAt,
                           @NonNull final String summary, @NonNull final String link, final long readAt) {
        Realm realm = Realm.getInstance(config);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(final Realm realm) {
                Read read = new Read();
                read.setTitle(title);
                read.setAuthor(author);
                read.setUpdatedAt(updatedAt);
                read.setSummary(summary);
                read.setLink(link);
                read.setReadAt(readAt);
                realm.insertOrUpdate(read);
            }
        });
        realm.close();
    }

    private RealmConfiguration createRealmConfiguration(@NonNull Context context) {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder()
            .name("awesome_blogs.realm")
            .schemaVersion(0)
            .migration(new RealmMigration() {
                @Override
                public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

                }
            });
        if (context.getPackageName().contains(".staging")) {
            builder.deleteRealmIfMigrationNeeded();
        }
        return builder.build();
    }

}
