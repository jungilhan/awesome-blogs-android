package org.petabytes.api.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.IntFunction;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.Supplier;

import org.petabytes.api.DataSource;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.realm.Realm;
import io.realm.RealmConfiguration;
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
        config = createRealmConfiguration();
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

    public Feed fillInCreatedAt(@NonNull Feed freshFeed, @NonNull Optional<Feed> cachedFeed) {
        if (cachedFeed.isPresent()) {
            Map<String, Long> createdAtMap = toCreatedAtMap(cachedFeed.get());
            for (int i = freshFeed.getEntries().size() - 1; i >= 0; i--) {
                Entry entry = freshFeed.getEntries().get(i);
                Long createdAt = createdAtMap.get(entry.getLink());
                entry.setCreatedAt(createdAt == null ? System.currentTimeMillis() : createdAt);
            }
        } else {
            for (int i = freshFeed.getEntries().size() - 1; i >= 0; i--) {
                freshFeed.getEntries().get(i).setCreatedAt(System.currentTimeMillis());
            }
        }
        return freshFeed;
    }

    public Feed sortByCreatedAt(@NonNull Feed feed) {
        Collections.sort(feed.getEntries(), new Comparator<Entry>() {
            @Override
            public int compare(Entry entry1, Entry entry2) {
                return Long.valueOf(entry2.getCreatedAt()).compareTo(entry1.getCreatedAt());
            }
        });
        return feed;
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

    public Observable<Pair<String, List<Entry>>> notifyFreshEntries(@NonNull final Feed feed) {
        return getFreshEntries(feed, new Supplier<List<Entry>>() {
            @Override
            public List<Entry> get() {
                return Collections.emptyList();
            }
        }).map(new Func1<List<Entry>, Pair<String, List<Entry>>>() {
            @Override
            public Pair<String, List<Entry>> call(@NonNull List<Entry> entries) {
                return new Pair<>(feed.getCategory(), entries);
            }
        }).doOnNext(new Action1<Pair<String, List<Entry>>>() {
            @Override
            public void call(Pair<String, List<Entry>> pair) {
                freshEntriesSubject.onNext(pair);
            }
        });
    }

    private Observable<List<Entry>> getFreshEntries(@NonNull final Feed feed, @NonNull final Supplier<List<Entry>> ifEmptyExistEntriesSupplier) {
        return getExistEntries(feed).map(new Func1<List<Entry>, List<Entry>>() {
            @Override
            public List<Entry> call(final List<Entry> existEntries) {
                return existEntries.isEmpty()
                    ? ifEmptyExistEntriesSupplier.get()
                    : Stream.of(feed.getEntries())
                    .filter(new Predicate<Entry>() {
                        @Override
                        public boolean test(Entry value) {
                            return !existEntries.contains(value);
                        }
                    })
                    .collect(Collectors.<Entry>toList());
            }
        });
    }

    private Observable<List<Entry>> getExistEntries(@NonNull final Feed feed) {
        return Observable.fromCallable(new Callable<List<Entry>>() {
            public List<Entry> call() throws Exception {
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
                    return realm.copyFromRealm(existEntries);
                } finally {
                    realm.close();
                }
            }
        });
    }

    private Map<String, Long> toCreatedAtMap(@NonNull Feed feed) {
        return Stream.of(feed.getEntries())
            .collect(Collectors.toMap(new Function<Entry, String>() {
                @Override
                public String apply(Entry entry) {
                    return entry.getLink();
                }
            }, new Function<Entry, Long>() {
                @Override
                public Long apply(Entry entry) {
                    return entry.getCreatedAt();
                }
            }));
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

    public Observable<Date> getExpiryDate(@NonNull String category) {
        final Realm realm = Realm.getInstance(config);
        return realm.where(Feed.class).equalTo("category", category).findAll().asObservable()
            .filter(new Func1<RealmResults<Feed>, Boolean>() {
                @Override
                public Boolean call(RealmResults<Feed> feeds) {
                    return !feeds.isEmpty();
                }
            })
            .map(new Func1<RealmResults<Feed>, Date>() {
                @Override
                public Date call(RealmResults<Feed> feeds) {
                    return new Date(feeds.get(0).getExpires());
                }
            })
            .doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    realm.close();
                }
            });

    }

    private RealmConfiguration createRealmConfiguration() {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder()
            .name("awesome_blogs.realm")
            .schemaVersion(1)
            .migration(new Migration());
        return builder.build();
    }

}
