package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.squareup.coordinators.Coordinators;

import org.petabytes.api.model.Entry;
import org.petabytes.api.model.Feed;
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
import retrofit2.Response;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static org.petabytes.awesomeblogs.feeds.FeedsCoordinator.Type.*;

class FeedsCoordinator extends Coordinator {

    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.feeds) VerticalViewPager pagerView;

    private final Context context;

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
        bind(AwesomeBlogsApp.get().api()
            .awesomeBlogs()
            .feeds("all")
            .filter(Response::isSuccessful)
            .map(Response::body)
            .map(Feed::getEntries)
            .map(FeedsCoordinator::categorize)
            .subscribeOn(Schedulers.io()), entries -> {
                Views.setGone(loadingView);
                Views.setVisible(pagerView);
                pagerView.setOffscreenPageLimit(2);
                pagerView.setAdapter(new PagerAdapter<>(entries, new PagerFactory<Map<Type, List<Entry>>>() {
                    @Override
                    public View create(@NonNull Map<Type, List<Entry>> entry) {
                        View view;
                        if (entry.containsKey(GRADIENT)) {
                            view = LayoutInflater.from(context).inflate(R.layout.entry_gradient, null, false);
                            Coordinators.bind(view, $ -> new EntryGradientCoordinator(context, entry.get(GRADIENT).get(0)));
                        } else if (entry.containsKey(DIAGONAL)) {
                            view = LayoutInflater.from(context).inflate(R.layout.entry_diagonal, null, false);
                            Coordinators.bind(view, $ -> new EntryDiagonalCoordinator(context, entry.get(DIAGONAL)));
                        } else if (entry.containsKey(ROWS)) {
                            view = LayoutInflater.from(context).inflate(R.layout.entry_rows, null, false);
                            Coordinators.bind(view, $ -> new EntryRowsCoordinator(context, entry.get(ROWS)));
                        } else {
                            throw new IllegalArgumentException("Invalid entry.");
                        }
                        return view;
                    }
                }));
                pagerView.getAdapter().notifyDataSetChanged();
            }, throwable -> Timber.e(throwable, throwable.getMessage()), () -> {});
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
