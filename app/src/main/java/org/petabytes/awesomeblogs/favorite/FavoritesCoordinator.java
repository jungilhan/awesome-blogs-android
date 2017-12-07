package org.petabytes.awesomeblogs.favorite;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.squareup.coordinators.Coordinators;

import org.jsoup.Jsoup;
import org.petabytes.api.source.local.Favorite;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.summary.SummaryActivity;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.Coordinator;
import org.petabytes.coordinator.RecyclerAdapter;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

class FavoritesCoordinator extends Coordinator {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.count) TextView countView;
    @BindView(R.id.empty) TextView emptyView;
    @BindView(R.id.recycler) RecyclerView recyclerView;

    private final Context context;
    private final Action0 closeAction;
    private RecyclerAdapter<Favorite> adapter;

    FavoritesCoordinator(@NonNull Context context, @NonNull Action0 closeAction) {
        this.context = context;
        this.closeAction = closeAction;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        recyclerView.setAdapter(adapter = new RecyclerAdapter<>(() -> {
            View v = LayoutInflater.from(context).inflate(R.layout.favorites_item, null, false);
            FavoriteItemCoordinator coordinator = new FavoriteItemCoordinator(context);
            Coordinators.bind(v, $ -> coordinator);
            return new RecyclerAdapter.ViewHolder<>(v, coordinator);
        }));

        bind(AwesomeBlogsApp.get().api().getFavorites(), entries -> {
            if (!entries.isEmpty()) {
                adapter.setItems(entries);
            } else {
                Views.setGone(recyclerView);
                Views.setVisible(emptyView);
            }
            countView.setText(String.valueOf(entries.size()));
        });
        bind(AwesomeBlogsApp.get().api().getFavorites()
            .first(), entries ->
                Analytics.event(Analytics.Event.VIEW_FAVORITES, Analytics.Param.SIZE, String.valueOf(entries.size())));
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        closeAction.call();
    }

    static class FavoriteItemCoordinator extends Coordinator implements RecyclerAdapter.OnBindViewHolderListener<Favorite> {

        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.summary) TextView summaryView;
        @BindView(R.id.author) TextView authorView;

        private final Context context;

        FavoriteItemCoordinator(@NonNull Context context) {
            this.context = context;
        }

        @Override
        public void onBindViewHolder(@NonNull Favorite favorite, int position) {
            titleView.setText(favorite.getTitle());
            authorView.setText(Favorite.getFormattedAuthorUpdatedAt(favorite));

            bind(Observable.just(favorite.getSummary())
                .map(summary -> Jsoup.parse(summary).text())
                .map(summary -> summary.substring(0, Math.min(200, summary.length())))
                .subscribeOn(Schedulers.io()), summary -> {
                    summaryView.setText(summary.trim());
                    titleView.post(() -> summaryView.setMaxLines(4 - titleView.getLineCount()));
                });

            getView().setOnClickListener($ -> {
                context.startActivity(SummaryActivity.intent(context, favorite.getLink(), Analytics.Param.FAVORITES));
                Analytics.event(Analytics.Event.VIEW_FAVORITES_ITEM, new HashMap<String, String>(2) {{
                    put(Analytics.Param.TITLE, favorite.getTitle());
                    put(Analytics.Param.LINK, favorite.getLink());
                }});
            });
            getView().setBackgroundResource(position % 2 == 0 ? R.color.white : R.color.background_row);
        }
    }
}
