package org.petabytes.awesomeblogs.digest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.petabytes.api.source.local.Entry;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.fcm.Notifications;
import org.petabytes.awesomeblogs.feeds.FeedsActivity;
import org.petabytes.awesomeblogs.util.Analytics;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class DigestService extends IntentService {

    private static final String TYPE = "type";

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
            .doOnNext(entries ->
                Analytics.event(Analytics.Event.SEND_DIGEST, new HashMap<String, String>(2) {{
                    put(Analytics.Param.TYPE, getType(intent) == 0 ? "morning" : "evening");
                    put(Analytics.Param.SIZE, String.valueOf(entries.size()));
                }}))
            .subscribe(entries ->
                Notifications.send(this, getString(R.string.digest_title, entries.size()),
                    createMessage(entries), FeedsActivity.intent(this, "all", entries.size())));

        AwesomeBlogsApp.get().api().getFeed("all", false)
            .onErrorResumeNext(Observable.empty())
            .subscribe();
    }

    private String createMessage(@NonNull List<Entry> entries) {
        String message;
        switch (entries.size()) {
            case 0:
                throw new IllegalStateException("Size cannot be zero.");
            case 1:
                message = getString(R.string.digest_message_1, entries.get(0).getTitle());
                break;
            case 2:
                message = getString(R.string.digest_message_2,
                    entries.get(0).getTitle(), entries.get(1).getTitle());
                break;
            case 3:
                message = getString(R.string.digest_message_3,
                    entries.get(0).getTitle(), entries.get(1).getTitle(), entries.get(2).getTitle());
                break;
            default:
                message = getString(R.string.digest_message_4,
                    entries.get(0).getTitle(), entries.get(1).getTitle(), entries.get(2).getTitle(), entries.size() - 3);
                break;
        }
        return message;
    }

    private static int getType(@Nullable Intent intent) {
        return intent == null ? 0 : intent.getIntExtra(TYPE, 0);
    }

    public static Intent intent(@NonNull Context context, @Schedulers.Type int type) {
        Intent intent = new Intent(context, DigestService.class);
        intent.putExtra(TYPE, type);
        return intent;
    }
}
