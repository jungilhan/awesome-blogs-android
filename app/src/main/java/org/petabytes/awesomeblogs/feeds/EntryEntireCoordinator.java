package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.petabytes.api.source.local.Entry;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.summary.SummaryActivity;
import org.petabytes.coordinator.Coordinator;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.schedulers.Schedulers;

class EntryEntireCoordinator extends Coordinator {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.author) TextView authorView;
    @BindView(R.id.summary) TextView summaryView;

    private final Context context;
    private final Entry entry;

    EntryEntireCoordinator(@NonNull Context context, @NonNull Entry entry) {
        this.context = context;
        this.entry = entry;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        bind(AwesomeBlogsApp.get().api()
            .isRead(entry.getLink()), isRead -> {
                titleView.setText(entry.getTitle());
                titleView.setAlpha(isRead ? 0.65f : 1f);
                authorView.setText(Entry.getFormattedAuthorUpdatedAt(entry));
            });
        bind(Observable.just(entry.getSummary().trim())
            .map(summary -> Jsoup.parse(summary).text())
            .subscribeOn(Schedulers.io()), summary -> summaryView.setText(summary));
        setBackground(view);
    }

    @OnClick(R.id.container)
    void onContainerClick() {
        context.startActivity(SummaryActivity.intent(context, entry.getLink()));
    }

    private void setBackground(@NonNull View view) {
        switch (new Random(System.identityHashCode(entry.getTitle())).nextInt(11)) {
            case 0: view.setBackgroundResource(R.color.background_12); break;
            case 1: view.setBackgroundResource(R.color.background_13); break;
            case 2: view.setBackgroundResource(R.color.background_14); break;
            case 3: view.setBackgroundResource(R.color.background_15); break;
            case 4: view.setBackgroundResource(R.color.background_16); break;
            case 5: view.setBackgroundResource(R.color.background_17); break;
            case 6: view.setBackgroundResource(R.color.background_18); break;
            case 7: view.setBackgroundResource(R.color.background_19); break;
            case 8: view.setBackgroundResource(R.color.background_20); break;
            case 9: view.setBackgroundResource(R.color.background_21); break;
            case 10: view.setBackgroundResource(R.color.background_22); break;
        }
    }
}
