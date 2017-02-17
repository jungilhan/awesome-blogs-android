package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.squareup.coordinators.Coordinators;

import org.petabytes.api.source.local.Entry;
import org.petabytes.api.source.local.Feed;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Views;
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
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import hugo.weaving.DebugLog;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.DIAGONAL;
import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.GRADIENT;
import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.ROWS;

class FeedsCoordinator extends Coordinator {

    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.feeds) VerticalViewPager pagerView;

    private final Context context;
    private @DrawerCoordinator.Category String category;

    enum Type {
        GRADIENT, DIAGONAL, ROWS
    }

    FeedsCoordinator(@NonNull Context context) {
        this.context = context;
    }

    @DebugLog
    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        pagerView.setOffscreenPageLimit(2);
    }

    void onCategorySelect(@DrawerCoordinator.Category String category) {
        Views.setVisible(loadingView);
        Views.setGone(pagerView);

        bind(AwesomeBlogsApp.get().api()
            .getFeed(category)
            .map(Feed::getEntries)
            .map(FeedsCoordinator::categorize)
            .subscribeOn(Schedulers.io()), entries -> {
                Views.setGone(loadingView);
                Views.setVisible(pagerView);
                pagerView.setAdapter(new PagerAdapter<>(entries, createPagerFactory()));
        }, throwable -> {
            Timber.e(throwable, throwable.getMessage());
            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }, () -> {});
    }

    @NonNull
    private PagerFactory<Map<Type, List<Entry>>> createPagerFactory() {
        return entry -> {
            View view1;
            if (entry.containsKey(GRADIENT)) {
                view1 = LayoutInflater.from(context).inflate(R.layout.entry_gradient, null, false);
                Coordinators.bind(view1, $ -> new EntryGradientCoordinator(context, entry.get(GRADIENT).get(0)));
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

    private static List<Map<Type, List<Entry>>> categorize(@NonNull List<Entry> entries) {
        List<Map<Type, List<Entry>>> categorized = new ArrayList<>();
        List<Entry> clone = new ArrayList<>(entries);
        int type = new Random().nextInt(2);
        if (type == 0 && clone.size() >= 2) {
            categorized.add(Collections.singletonMap(DIAGONAL, Arrays.asList(clone.remove(0), clone.remove(0))));
        } else {
            categorized.add(Collections.singletonMap(GRADIENT, Arrays.asList(clone.remove(0))));
        }

        while (clone.size() > 0) {
            type = new Random().nextInt(3);
            if (type == 1 && clone.size() >= 2) {
                categorized.add(Collections.singletonMap(DIAGONAL, Arrays.asList(clone.remove(0), clone.remove(0))));
            } else if (type == 2 && clone.size() >= 5) {
                categorized.add(Collections.singletonMap(ROWS, Arrays.asList(clone.remove(0), clone.remove(0),
                    clone.remove(0), clone.remove(0), clone.remove(0))));
            } else {
                categorized.add(Collections.singletonMap(GRADIENT, Arrays.asList(clone.remove(0))));
            }
        }
        return categorized;
    }
}
