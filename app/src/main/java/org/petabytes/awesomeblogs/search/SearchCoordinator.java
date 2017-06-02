package org.petabytes.awesomeblogs.search;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.annimon.stream.function.Supplier;
import com.jakewharton.rxrelay.PublishRelay;
import com.squareup.coordinators.Coordinators;

import org.jsoup.Jsoup;
import org.petabytes.api.source.local.Entry;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.summary.SummaryActivity;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Strings;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.Coordinator;
import org.petabytes.coordinator.RecyclerAdapter;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

class SearchCoordinator extends Coordinator {

    @BindView(R.id.search) EditText searchView;
    @BindView(R.id.recycler) RecyclerView recyclerView;

    private final Context context;
    private final Action0 closeAction;
    private RecyclerAdapter<Entry> adapter;
    private PublishRelay<String> keywordRelay;

    SearchCoordinator(@NonNull Context context, @NonNull Action0 closeAction) {
        this.context = context;
        this.closeAction = closeAction;
        this.keywordRelay = PublishRelay.create();
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        recyclerView.setAdapter(adapter = new RecyclerAdapter<>(() -> {
            View v = LayoutInflater.from(context).inflate(R.layout.search_item, null, false);
            SearchItemCoordinator coordinator = new SearchItemCoordinator(context, () -> searchView.getText().toString());
            Coordinators.bind(v, $ -> coordinator);
            return new RecyclerAdapter.ViewHolder<>(v, coordinator);
        }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    Views.hideSoftInput(searchView);
                }
            }
        });

        bind(keywordRelay.debounce(250, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .flatMap(keyword ->
                AwesomeBlogsApp.get().api().search(keyword)), entries -> {
                    if (!entries.isEmpty()) {
                        recyclerView.scrollToPosition(0);
                    }
                    adapter.setItems(entries);
                });

        Analytics.event(Analytics.Event.VIEW_SEARCH);
    }

    @OnTextChanged(R.id.search)
    void onSearchChanged(@NonNull Editable keyword) {
        keywordRelay.call(keyword.toString().trim());
        Views.setVisibleOrGone(recyclerView, keyword.length() > 0);
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        closeAction.call();
    }

    static class SearchItemCoordinator extends Coordinator implements RecyclerAdapter.OnBindViewHolderListener<Entry> {

        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.summary) TextView summaryView;
        @BindView(R.id.author) TextView authorView;

        private final Context context;
        private final Supplier<String> keywordSupplier;

        SearchItemCoordinator(@NonNull Context context, @NonNull Supplier<String> keywordSupplier) {
            this.context = context;
            this.keywordSupplier = keywordSupplier;
        }

        @Override
        public void onBindViewHolder(@NonNull Entry entry, int position) {
            titleView.setTypeface(titleView.getTypeface(), Typeface.BOLD);
            titleView.setText(Strings.colorizeBackground(entry.getTitle(),
                keywordSupplier.get(), context.getResources().getColor(R.color.search), true));
            authorView.setText(Entry.getFormattedAuthorUpdatedAt(entry));

            bind(Observable.just(entry.getSummary())
                .map(summary -> Jsoup.parse(summary).text())
                .map(summary -> summary.substring(0, Math.min(200, summary.length())))
                .subscribeOn(Schedulers.io()), summary -> {
                    summaryView.setText(summary.trim());
                    titleView.post(() -> summaryView.setMaxLines(4 - titleView.getLineCount()));
                });

            getView().setOnClickListener($ -> {
                context.startActivity(SummaryActivity.intent(context, entry.getLink(), Analytics.Param.SEARCH));
                Analytics.event(Analytics.Event.VIEW_SEARCH_ITEM, new HashMap<String, String>(2) {{
                    put(Analytics.Param.TITLE, entry.getTitle());
                    put(Analytics.Param.LINK, entry.getLink());
                }});
            });
            getView().setBackgroundResource(position % 2 == 0 ? R.color.white : R.color.background_row);
        }
    }
}
