package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.IntStream;

import org.jsoup.Jsoup;
import org.petabytes.api.source.local.Entry;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.summary.SummaryActivity;
import org.petabytes.coordinator.Coordinator;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindViews;
import butterknife.OnClick;
import rx.Observable;
import rx.schedulers.Schedulers;

class EntryRowsCoordinator extends Coordinator {

    @BindViews({R.id.title_1, R.id.title_2, R.id.title_3,
        R.id.title_4, R.id.title_5}) TextView[] titleViews;
    @BindViews({R.id.author_1, R.id.author_2, R.id.author_3,
        R.id.author_4, R.id.author_5}) TextView[] authorViews;
    @BindViews({R.id.summary_1, R.id.summary_2, R.id.summary_3,
        R.id.summary_4, R.id.summary_5}) TextView[] summaryViews;

    private final Context context;
    private final List<Entry> entries;

    EntryRowsCoordinator(@NonNull Context context, @NonNull List<Entry> entries) {
        this.context = context;
        this.entries = entries;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        IntStream.range(0, entries.size())
            .forEach(i -> {
                bind(AwesomeBlogsApp.get().api()
                    .isRead(entries.get(i).getLink()), isRead -> {
                        titleViews[i].setText(entries.get(i).getTitle());
                        titleViews[i].setTextColor(context.getResources().getColor(isRead ? R.color.grey : R.color.black));
                        authorViews[i].setText(Entry.getFormattedAuthorUpdatedAt(entries.get(i)));
                    });
                bind(Observable.just(entries.get(i).getSummary())
                    .map(summary -> Jsoup.parse(summary).text())
                    .delay(200, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io()), summary -> {
                        summaryViews[i].setText(summary.trim());
                        summaryViews[i].setMaxLines(3 - titleViews[i].getLineCount());
                    });
            });
    }

    @OnClick({R.id.row_1, R.id.row_2, R.id.row_3, R.id.row_4, R.id.row_5})
    void onRowClick(View view) {
        Entry entry;
        switch (view.getId()) {
            case R.id.row_1:
                entry = entries.get(0);
                break;
            case R.id.row_2:
                entry = entries.get(1);
                break;
            case R.id.row_3:
                entry = entries.get(2);
                break;
            case R.id.row_4:
                entry = entries.get(3);
                break;
            case R.id.row_5:
                entry = entries.get(4);
                break;
            default:
                throw new RuntimeException("Invalid id");
        }
        context.startActivity(SummaryActivity.intent(context,
            entry.getTitle(), entry.getAuthor(), entry.getUpdatedAt(), entry.getSummary(), entry.getLink()));
    }
}
