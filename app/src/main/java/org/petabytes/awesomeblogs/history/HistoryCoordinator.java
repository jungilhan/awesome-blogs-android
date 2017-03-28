package org.petabytes.awesomeblogs.history;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.squareup.coordinators.Coordinators;

import org.jsoup.Jsoup;
import org.petabytes.api.source.local.Read;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.summary.SummaryActivity;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Strings;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.Coordinator;
import org.petabytes.coordinator.RecyclerAdapter;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static android.R.attr.entries;

class HistoryCoordinator extends Coordinator {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.count) TextView countView;
    @BindView(R.id.empty) TextView emptyView;
    @BindView(R.id.recycler) RecyclerView recyclerView;

    private final Context context;
    private final Action0 onCloseAction;
    private RecyclerAdapter<Read> adapter;

    HistoryCoordinator(@NonNull Context context, @NonNull Action0 onCloseAction) {
        this.context = context;
        this.onCloseAction = onCloseAction;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        titleView.setTypeface(titleView.getTypeface(), Typeface.BOLD);
        recyclerView.setAdapter(adapter = new RecyclerAdapter<>(() -> {
            View v = LayoutInflater.from(context).inflate(R.layout.history_item, null, false);
            HistoryItemCoordinator coordinator = new HistoryItemCoordinator(context);
            Coordinators.bind(v, $ -> coordinator);
            return new RecyclerAdapter.ViewHolder<>(v, coordinator);
        }));

        bind(AwesomeBlogsApp.get().api().getHistory(), entries -> {
            if (!entries.isEmpty()) {
                adapter.setItems(entries);
                countView.setText(String.valueOf(entries.size()));
            } else {
                Views.setGone(recyclerView);
                Views.setVisible(emptyView);
            }
        });
        bind(AwesomeBlogsApp.get().api().getHistory()
            .first(), entries ->
                Analytics.event(Analytics.Event.VIEW_HISTORY, Analytics.Param.SIZE, String.valueOf(entries.size())));
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        onCloseAction.call();
    }

    static class HistoryItemCoordinator extends Coordinator implements RecyclerAdapter.OnBindViewHolderListener<Read> {

        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.summary) TextView summaryView;
        @BindView(R.id.author) TextView authorView;

        private final Context context;

        HistoryItemCoordinator(@NonNull Context context) {
            this.context = context;
        }

        @Override
        public void onBindViewHolder(@NonNull Read read, int position) {
            titleView.setText(read.getTitle());
            titleView.setTypeface(titleView.getTypeface(), Typeface.BOLD);
            authorView.setText(Read.getFormattedAuthorUpdatedAt(read));

            bind(Observable.just(read.getSummary())
                .map(summary -> Jsoup.parse(summary).text())
                .map(summary -> summary.substring(0, Math.min(200, summary.length())))
                .subscribeOn(Schedulers.io()), summary -> {
                    summaryView.setText(summary.trim());
                    titleView.post(() -> summaryView.setMaxLines(4 - titleView.getLineCount()));
                });

            getView().setOnClickListener($ -> {
                context.startActivity(SummaryActivity.intent(context, read.getLink(), Analytics.Param.HISTORY));
                Analytics.event(Analytics.Event.VIEW_HISTORY_ITEM, new HashMap<String, String>(2) {{
                    put(Analytics.Param.TITLE, read.getTitle());
                    put(Analytics.Param.LINK, read.getLink());
                }});
            });
            getView().setBackgroundResource(position % 2 == 0 ? R.color.white : R.color.background_row);
        }
    }
}
