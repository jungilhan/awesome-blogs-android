package org.petabytes.awesomeblogs.author;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.squareup.coordinators.Coordinators;

import org.jsoup.Jsoup;
import org.petabytes.api.source.local.Entry;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.summary.SummaryActivity;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.coordinator.Coordinator;
import org.petabytes.coordinator.RecyclerAdapter;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

class AuthorCoordinator extends Coordinator {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.count) TextView countView;
    @BindView(R.id.recycler) RecyclerView recyclerView;

    private final Context context;
    private final String author;
    private final Action0 closeAction;
    private RecyclerAdapter<Entry> adapter;

    AuthorCoordinator(@NonNull Context context, @NonNull String author, @NonNull Action0 closeAction) {
        this.context = context;
        this.author = author;
        this.closeAction = closeAction;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        recyclerView.setAdapter(adapter = new RecyclerAdapter<>(() -> {
            View v = LayoutInflater.from(context).inflate(R.layout.author_item, null, false);
            AuthorItemCoordinator coordinator = new AuthorItemCoordinator(context);
            Coordinators.bind(v, $ -> coordinator);
            return new RecyclerAdapter.ViewHolder<>(v, coordinator);
        }));

        bind(AwesomeBlogsApp.get().api().getEntries(author), entries -> {
            adapter.setItems(entries);
            countView.setText(String.valueOf(entries.size()));
        });
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        closeAction.call();
    }

    static class AuthorItemCoordinator extends Coordinator implements RecyclerAdapter.OnBindViewHolderListener<Entry> {

        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.summary) TextView summaryView;
        @BindView(R.id.author) TextView authorView;

        private final Context context;

        AuthorItemCoordinator(@NonNull Context context) {
            this.context = context;
        }

        @Override
        public void onBindViewHolder(@NonNull Entry entry, int position) {
            titleView.setText(entry.getTitle());
            authorView.setText(Entry.getFormattedAuthorUpdatedAt(entry));

            bind(Observable.just(entry.getSummary())
                .map(summary -> Jsoup.parse(summary).text())
                .map(summary -> summary.substring(0, Math.min(200, summary.length())))
                .subscribeOn(Schedulers.io()), summary -> {
                    summaryView.setText(summary.trim());
                    titleView.post(() -> summaryView.setMaxLines(4 - titleView.getLineCount()));
                });

            getView().setOnClickListener($ -> {
                context.startActivity(SummaryActivity.intent(context, entry.getLink(), Analytics.Param.AUTHOR));
                Analytics.event(Analytics.Event.VIEW_AUTHOR, new HashMap<String, String>(3) {{
                    put(Analytics.Param.TITLE, entry.getTitle());
                    put(Analytics.Param.LINK, entry.getLink());
                    put(Analytics.Param.AUTHOR, entry.getAuthor());
                }});
            });
            getView().setBackgroundResource(position % 2 == 0 ? R.color.white : R.color.background_row);
        }
    }
}
