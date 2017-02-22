package org.petabytes.api.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.petabytes.api.DataSource;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.functions.Func1;

public class AwesomeBlogsLocalSource implements DataSource {

    @VisibleForTesting
    final RealmConfiguration config;

    public AwesomeBlogsLocalSource(@NonNull Context context) {
        Realm.init(context);
        config = createRealmConfiguration(context);
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
