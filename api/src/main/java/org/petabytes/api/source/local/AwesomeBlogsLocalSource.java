package org.petabytes.api.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import org.petabytes.api.DataSource;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import rx.Observable;
import rx.Subscriber;

public class AwesomeBlogsLocalSource implements DataSource {

    private RealmConfiguration config;

    public AwesomeBlogsLocalSource(@NonNull Context context) {
        Realm.init(context);
        config = createRealmConfiguration();
    }

    public void saveFeed(final Feed feed) {
        Realm realm = Realm.getInstance(config);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(final Realm realm) {
                realm.insertOrUpdate(feed);
            }
        });
        realm.close();
    }

    @Override
    public Observable<Feed> getFeed(@NonNull final String category) {
        return Observable.create(new Observable.OnSubscribe<Feed>() {
            @Override
            public void call(Subscriber<? super Feed> subscriber) {
                Realm realm = Realm.getInstance(config);
                Feed feed = realm.where(Feed.class).equalTo("category", category).findFirst();
                subscriber.onNext(feed != null ? realm.copyFromRealm(feed) : null);
                subscriber.onCompleted();
                realm.close();
            }
        });
    }

    private RealmConfiguration createRealmConfiguration() {
        return new RealmConfiguration.Builder()
            .name("awesome_blogs.realm")
            .schemaVersion(0)
            .migration(new RealmMigration() {
                @Override
                public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

                }
            })
            .build();
    }
}
