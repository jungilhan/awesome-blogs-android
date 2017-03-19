package org.petabytes.awesomeblogs.digest;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.fcm.Notifications;
import org.petabytes.awesomeblogs.util.Analytics;

import java.util.concurrent.TimeUnit;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class DigestService extends IntentService {

    @DebugLog
    public DigestService() {
        super("DigestService");
    }

    @DebugLog
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AwesomeBlogsApp.get().api().getFreshEntries()
            .map(pair -> pair.second)
            .filter(pair -> !pair.isEmpty())
            .first()
            .timeout(1, TimeUnit.MINUTES)
            .onErrorResumeNext(Observable.empty())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext($ -> Analytics.event(Analytics.Event.SEND_DIGEST))
            .subscribe(entries ->
                Notifications.send(this, getString(R.string.digest_title, entries.size()), entries.size() == 1
                    ? getString(R.string.fresh_entries_title_0, entries.get(0).getTitle())
                    : getString(R.string.fresh_entries_title_1, entries.get(0).getTitle(), (entries.size() - 1))));

        AwesomeBlogsApp.get().api().getFeed("all", false)
            .onErrorResumeNext(Observable.empty())
            .subscribe();
    }
}
