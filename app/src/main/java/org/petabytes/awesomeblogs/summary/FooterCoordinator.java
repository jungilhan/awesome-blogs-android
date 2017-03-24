package org.petabytes.awesomeblogs.summary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.TextView;

import org.petabytes.api.source.local.Entry;
import org.petabytes.api.source.local.Feed;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Preferences;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.Coordinator;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func1;

class FooterCoordinator extends Coordinator {

    @BindView(R.id.previous) View previousView;
    @BindView(R.id.previous_title) TextView previousTitleView;
    @BindView(R.id.next) View nextView;
    @BindView(R.id.next_title) TextView nextTitleView;

    private final Context context;
    private final String link;

    FooterCoordinator(@NonNull Context context, @NonNull String link) {
        this.context = context;
        this.link = link;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        bind(Preferences.category().asObservable()
            .first()
            .flatMap(new Func1<String, Observable<Pair<Feed, Entry>>>() {
                @Override
                public Observable<Pair<Feed, Entry>> call(String category) {
                    return Observable.combineLatest(
                        AwesomeBlogsApp.get().api().getFeed(category),
                        AwesomeBlogsApp.get().api().getEntry(link), Pair::new);
                }
            }), pair -> {
                List<Entry> entries = pair.first.getEntries();
                int index = entries.indexOf(pair.second);
                if (index >= 1) {
                    Entry previousEntry = entries.get(index - 1);
                    previousView.setTag(previousEntry);
                    previousTitleView.setText(previousEntry.getTitle());
                    Views.setVisible(previousView);
                }
                if (index < entries.size() - 1) {
                    Entry nextEntry = entries.get(index + 1);
                    nextView.setTag(nextEntry);
                    nextTitleView.setText(nextEntry.getTitle());
                    Views.setVisible(nextView);
                }
            });
    }

    void show() {
        Views.setVisibleOrGone(getView(), Views.isVisible(previousView) || Views.isVisible(nextView));
    }

    @OnClick({R.id.previous, R.id.next})
    void onEntryClick(@NonNull View view) {
        Entry entry = (Entry) view.getTag();
        context.startActivity(SummaryActivity.intent(context, entry.getLink()));
        Analytics.event(Analytics.Event.VIEW_AROUND, new HashMap<String, String>(2) {{
            put(Analytics.Param.TITLE, entry.getTitle());
            put(Analytics.Param.LINK, entry.getLink());
        }});
    }
}
