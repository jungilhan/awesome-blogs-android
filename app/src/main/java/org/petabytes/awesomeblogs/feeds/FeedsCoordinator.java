package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.squareup.coordinators.Coordinators;

import org.petabytes.api.source.local.Entry;
import org.petabytes.api.source.local.Feed;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Alerts;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.Activity;
import org.petabytes.coordinator.Coordinator;
import org.petabytes.coordinator.PagerAdapter;
import org.petabytes.coordinator.PagerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import hugo.weaving.DebugLog;
import rx.functions.Action3;
import rx.schedulers.Schedulers;

import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.DIAGONAL;
import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.ENTIRE;
import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.ROWS;

class FeedsCoordinator extends Coordinator {

    @BindView(R.id.refresh) SwipeRefreshLayout refreshView;
    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.feeds) VerticalViewPager pagerView;
    @BindView(R.id.progress_bar) SmoothProgressBar progressBar;

    private final Context context;
    private final Action3<Integer, Integer, Integer> onPageSelectedAction;
    private @DrawerCoordinator.Category String category;
    private ViewPager.SimpleOnPageChangeListener onPageChangeListener;

    enum Type {
        ENTIRE, DIAGONAL, ROWS
    }

    FeedsCoordinator(@NonNull Context context, @NonNull Action3<Integer, Integer, Integer> onPageSelectedAction) {
        this.context = context;
        this.onPageSelectedAction = onPageSelectedAction;
    }

    @DebugLog
    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        refreshView.setOnRefreshListener(() -> {
            load(category, true);
            Analytics.event(Analytics.Event.REFRESH);
        });
        refreshView.setColorSchemeResources(R.color.colorAccent,
            R.color.background_1, R.color.background_22, R.color.background_6);

        pagerView.setOnPageChangeListener(onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                onPageSelectedAction.call(position, pagerView.getAdapter().getCount(), getForegroundColor(position));
                progressBar.setSmoothProgressDrawableColors(getProgressBarColors(position));
                refreshView.setEnabled(refreshView.isRefreshing() || position == 0);
            }
        });

        bind(AwesomeBlogsApp.get().api()
            .getFreshEntries()
            .filter(pair -> TextUtils.equals(category, pair.first) && !pair.second.isEmpty()), this::notifyFreshEntries);

        bind(AwesomeBlogsApp.get().api()
            .getSilentRefresh()
            .filter(pair -> TextUtils.equals(category, pair.first))
            .map(pair -> pair.second), isRefreshing -> {
                Views.setVisibleOrGone(progressBar, isRefreshing);
                progressBar.setSmoothProgressDrawableColors(getProgressBarColors(0));
            });
    }

    @DebugLog
    void onCategorySelect(@DrawerCoordinator.Category String category) {
        load(this.category = category, false);
    }

    private void load(@DrawerCoordinator.Category String category, boolean refresh) {
        if (!refresh) {
            Views.setVisible(loadingView);
            Views.setGone(pagerView);
            refreshView.setEnabled(false);
        }
        Views.setGone(progressBar);

        bind(AwesomeBlogsApp.get().api()
            .getFeed(category, refresh)
            .filter($ -> TextUtils.equals(this.category, category))
            .map(Feed::getEntries)
            .map(this::categorize)
            .subscribeOn(Schedulers.io()), this::onLoad, $ -> onLoadError());
    }

    private void onLoad(@NonNull List<Map<Type, List<Entry>>> entries) {
        Views.setGone(loadingView);
        Views.setVisible(pagerView);
        refreshView.setRefreshing(false);
        refreshView.setEnabled(true);
        pagerView.setAdapter(new PagerAdapter<>(entries, createPagerFactory()));
        pagerView.post(() -> onPageChangeListener.onPageSelected(pagerView.getCurrentItem()));
    }

    private void onLoadError() {
        Views.setGone(loadingView);
        refreshView.setRefreshing(false);
        refreshView.setEnabled(true);
        Alerts.show((Activity) context, R.string.error_title, R.string.error_unknown_feed);
    }

    private PagerFactory<Map<Type, List<Entry>>> createPagerFactory() {
        return entry -> {
            View view1;
            if (entry.containsKey(ENTIRE)) {
                view1 = LayoutInflater.from(context).inflate(R.layout.entry_entire, null, false);
                Coordinators.bind(view1, $ -> new EntryEntireCoordinator(context, entry.get(ENTIRE).get(0)));
            } else if (entry.containsKey(DIAGONAL)) {
                view1 = LayoutInflater.from(context).inflate(R.layout.entry_diagonal, null, false);
                Coordinators.bind(view1, $ -> new EntryDiagonalCoordinator(context, entry.get(DIAGONAL)));
            } else if (entry.containsKey(ROWS)) {
                view1 = LayoutInflater.from(context).inflate(R.layout.entry_rows, null, false);
                Coordinators.bind(view1, $ -> new EntryRowsCoordinator(context, entry.get(ROWS)));
            } else {
                throw new IllegalArgumentException("Invalid entry.");
            }
            return view1;
        };
    }

    private List<Map<Type, List<Entry>>> categorize(@NonNull List<Entry> entries) {
        List<Map<Type, List<Entry>>> categorized = new ArrayList<>();
        List<Entry> clone = new ArrayList<>(entries);
        int type = new Random().nextInt(2);
        if (type == 0 && clone.size() >= 2) {
            categorized.add(Collections.singletonMap(DIAGONAL, Arrays.asList(clone.remove(0), clone.remove(0))));
        } else {
            categorized.add(Collections.singletonMap(ENTIRE, Arrays.asList(clone.remove(0))));
        }

        boolean isPortrait = context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
        while (clone.size() > 0) {
            type = new Random().nextInt(3);
            if (type == 1 && clone.size() >= 2) {
                categorized.add(Collections.singletonMap(DIAGONAL, Arrays.asList(clone.remove(0), clone.remove(0))));
            } else if (type == 2 && clone.size() >= (isPortrait ? 4 : 3)) {
                List<Entry> rows = isPortrait
                    ? Arrays.asList(clone.remove(0), clone.remove(0), clone.remove(0), clone.remove(0))
                    : Arrays.asList(clone.remove(0), clone.remove(0), clone.remove(0));
                categorized.add(Collections.singletonMap(ROWS, rows));
            } else {
                categorized.add(Collections.singletonMap(ENTIRE, Arrays.asList(clone.remove(0))));
            }
        }
        return categorized;
    }

    @ColorInt
    private int getForegroundColor(int position) {
        Map<Type, List<Entry>> map = (Map<Type, List<Entry>>) ((PagerAdapter) pagerView.getAdapter()).getItem(position);
        if (map.keySet().contains(ENTIRE)) {
            return context.getResources().getColor(R.color.white);
        } else {
            return context.getResources().getColor(R.color.colorPrimaryDark);
        }
    }

    @ColorInt
    private int[] getProgressBarColors(int position) {
        Map<Type, List<Entry>> map = (Map<Type, List<Entry>>) ((PagerAdapter) pagerView.getAdapter()).getItem(position);
        if (map.keySet().contains(ENTIRE)) {
            return new int[] {context.getResources().getColor(R.color.white)};
        } else {
            return context.getResources().getIntArray(R.array.progress_bar);
        }
    }

    private void notifyFreshEntries(@NonNull Pair<String, List<Entry>> pair) {
        Analytics.event(Analytics.Event.NOTIFY_FRESH_ENTRIES);
        TSnackbar snack = TSnackbar.make(getView(),
            pair.second.size() == 1
                ? context.getString(R.string.fresh_entries_title_0, pair.second.get(0).getTitle())
                : context.getString(R.string.fresh_entries_title_1, pair.second.get(0).getTitle(), (pair.second.size() - 1)),
            TSnackbar.LENGTH_LONG);

        TextView messageView = (TextView) snack.getView().findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        messageView.setTextColor(Color.WHITE);
        messageView.setMaxLines(2);
        snack.getView().setBackgroundResource(R.color.colorPrimaryDark);
        snack.setActionTextColor(context.getResources().getColor(R.color.colorAccent));
        snack.setMaxWidth(3000);
        snack.getView().setOnClickListener($ -> {
            load(category, false);
            snack.dismiss();
            Analytics.event(Analytics.Event.VIEW_FRESH_ENTRIES);
        });
        snack.show();
    }
}
