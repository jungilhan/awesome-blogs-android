package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
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
import rx.functions.Action2;
import rx.functions.Action3;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.DIAGONAL;
import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.GRADIENT;
import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.ROWS;

class FeedsCoordinator extends Coordinator {

    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.feeds) VerticalViewPager pagerView;

    private final Context context;
    private final Action3<Integer, Integer, Integer> onPagerSelectedAction;

    enum Type {
        GRADIENT, DIAGONAL, ROWS
    }

    FeedsCoordinator(@NonNull Context context, @NonNull Action3<Integer, Integer, Integer> onPagerSelectedAction) {
        this.context = context;
        this.onPagerSelectedAction = onPagerSelectedAction;
    }

    @DebugLog
    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        pagerView.setOffscreenPageLimit(1);
        pagerView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                onPagerSelectedAction.call(position, pagerView.getAdapter().getCount(), getForegroundColor(position));
            }
        });
    }

    void onCategorySelect(@DrawerCoordinator.Category String category) {
        Views.setVisible(loadingView);
        Views.setGone(pagerView);

        bind(AwesomeBlogsApp.get().api()
            .getFeed(category)
            .map(Feed::getEntries)
            .map(this::categorize)
            .subscribeOn(Schedulers.io()), entries -> {
                Views.setGone(loadingView);
                Views.setVisible(pagerView);
                pagerView.setAdapter(new PagerAdapter<>(entries, createPagerFactory()));
                onPagerSelectedAction.call(0, entries.size(), getForegroundColor(0));
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

    private List<Map<Type, List<Entry>>> categorize(@NonNull List<Entry> entries) {
        List<Map<Type, List<Entry>>> categorized = new ArrayList<>();
        List<Entry> clone = new ArrayList<>(entries);
        int type = new Random().nextInt(2);
        if (type == 0 && clone.size() >= 2) {
            categorized.add(Collections.singletonMap(DIAGONAL, Arrays.asList(clone.remove(0), clone.remove(0))));
        } else {
            categorized.add(Collections.singletonMap(GRADIENT, Arrays.asList(clone.remove(0))));
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
                categorized.add(Collections.singletonMap(GRADIENT, Arrays.asList(clone.remove(0))));
            }
        }
        return categorized;
    }

    @ColorInt
    private int getForegroundColor(int position) {
        Map<Type, List<Entry>> map = (Map<Type, List<Entry>>) ((PagerAdapter) pagerView.getAdapter()).getItem(position);
        if (map.keySet().contains(GRADIENT)) {
            return context.getResources().getColor(R.color.white);
        } else {
            return context.getResources().getColor(R.color.colorPrimaryDark);
        }
    }
}
