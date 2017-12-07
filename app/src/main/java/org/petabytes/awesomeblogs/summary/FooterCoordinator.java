package org.petabytes.awesomeblogs.summary;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.Stream;

import org.petabytes.api.source.local.Entry;
import org.petabytes.api.source.local.Feed;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.author.AuthorActivity;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Preferences;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.Coordinator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func1;

import static android.R.attr.entries;

class FooterCoordinator extends Coordinator {

    @BindView(R.id.author_entries) LinearLayoutCompat authorEntries;
    @BindView(R.id.divider) View dividerView;
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
        bind(AwesomeBlogsApp.get().api().getEntry(link)
            .flatMap(entry -> AwesomeBlogsApp.get().api().getEntries(entry.getAuthor())), entries -> {
                Stream.of(entries.subList(0, Math.min(6, entries.size())))
                    .forEach(entry -> {
                        if (TextUtils.equals(entry.getLink(), link)) {
                            return;
                        }
                        TextView entryView = (TextView) LayoutInflater
                            .from(context).inflate(R.layout.footer_author_entry, null, false);
                        entryView.setText(entry.getTitle());
                        entryView.setOnClickListener($ -> {
                            context.startActivity(SummaryActivity.intent(context, entry.getLink(), Analytics.Param.AUTHOR));
                            Analytics.event(Analytics.Event.VIEW_AUTHOR, new HashMap<String, String>(3) {{
                                put(Analytics.Param.TITLE, entry.getTitle());
                                put(Analytics.Param.LINK, entry.getLink());
                                put(Analytics.Param.AUTHOR, entry.getAuthor());
                            }});
                        });
                        authorEntries.addView(entryView);
                        Views.setVisible(authorEntries, dividerView);
                    });

                if (entries.size() > 5) {
                    View moreView = LayoutInflater.from(context).inflate(R.layout.footer_author_more, (ViewGroup) view, false);
                    moreView.setOnClickListener($ -> context.startActivity(AuthorActivity.intent(context, entries.get(0).getAuthor())));
                    authorEntries.addView(moreView);
                }
            });

        bind(Preferences.category().asObservable()
            .flatMap(category -> Observable.combineLatest(
                AwesomeBlogsApp.get().api().getFeed(category),
                AwesomeBlogsApp.get().api().getEntry(link), Pair::new))
            .first(), pair -> {
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

    void show(int progress) {
        Views.setVisibleOrGone(getView(), progress > 70 && (Views.isVisible(previousView) || Views.isVisible(nextView)));
    }

    @OnClick({R.id.previous, R.id.next})
    void onEntryClick(@NonNull View view) {
        Entry entry = (Entry) view.getTag();
        context.startActivity(SummaryActivity.intent(context, entry.getLink(), Analytics.Param.SIBLING));
        Analytics.event(Analytics.Event.VIEW_SIBLING, new HashMap<String, String>(2) {{
            put(Analytics.Param.TITLE, entry.getTitle());
            put(Analytics.Param.LINK, entry.getLink());
        }});
    }
}
