package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.IntStream;

import org.jsoup.Jsoup;
import org.petabytes.api.model.Entry;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Dates;
import org.petabytes.awesomeblogs.util.Strings;
import org.petabytes.awesomeblogs.web.WebViewActivity;
import org.petabytes.coordinator.Coordinator;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
                titleViews[i].setText(entries.get(i).getTitle());
                authorViews[i].setText("by " + entries.get(i).getAuthor() + "  /  " + Dates.getRelativeTimeString(
                    Dates.getDefaultDateFormats().parse(entries.get(i).getUpdatedAt(), new ParsePosition(0)).getTime()));

                bind(Observable.just(entries.get(i).getSummary())
                    .map(summary -> Jsoup.parse(summary).text())
                    .subscribeOn(Schedulers.io()), summary -> {
                        summaryViews[i].setText(summary.trim());
                        summaryViews[i].setMaxLines(3 - titleViews[i].getLineCount());
                    });
            });
    }

    @OnClick({R.id.row_1, R.id.row_2, R.id.row_3, R.id.row_4, R.id.row_5})
    void onRowClick(View view) {
        String url = Strings.EMPTY;
        switch (view.getId()) {
            case R.id.row_1:
                url = entries.get(0).getLink();
                break;
            case R.id.row_2:
                url = entries.get(1).getLink();
                break;
            case R.id.row_3:
                url = entries.get(2).getLink();
                break;
            case R.id.row_4:
                url = entries.get(3).getLink();
                break;
            case R.id.row_5:
                url = entries.get(4).getLink();
                break;
        }
        context.startActivity(WebViewActivity.intent(context, url));
    }
}
