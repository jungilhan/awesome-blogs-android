package org.petabytes.awesomeblogs.summary;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.overzealous.remark.Remark;

import org.petabytes.api.source.local.Entry;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Alerts;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.coordinator.Activity;
import org.petabytes.coordinator.Coordinator;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import eu.fiskur.markdownview.MarkdownView;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.petabytes.awesomeblogs.R.id.summary;

class SummaryCoordinator extends Coordinator {

    @BindView(R.id.bottom_sheet) BottomSheetLayout bottomSheetView;
    @BindView(summary) MarkdownView summaryView;

    private final Context context;
    private final String link;
    private final Action1<Integer> onLoading;
    private final Action0 onCloseAction;
    private Entry entry;

    SummaryCoordinator(@NonNull Context context, @NonNull String link,
                       @NonNull Action1<Integer> onLoadingAction, @NonNull Action0 onCloseAction) {
        this.context = context;
        this.link = link;
        this.onLoading = onLoadingAction;
        this.onCloseAction = onCloseAction;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        bind(AwesomeBlogsApp.get().api().getEntry(link)
            .doOnNext(entry -> this.entry = entry), this::onEntryChanged);

        summaryView.setOnProgressChangedListener(onLoading::call);
        summaryView.setOnOverrideUrlAction(
            url -> {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                Analytics.event(Analytics.Event.OPEN_IN_BROWSER, new HashMap<String, String>(2) {{
                    put(Analytics.Param.TITLE, entry.getTitle());
                    put(Analytics.Param.LINK, link);
                }});
            },
            () -> Alerts.show((Activity) context, R.string.error_title, R.string.error_invalid_link));
    }

    private void onEntryChanged(@NonNull Entry entry) {
        bind(Observable.just(entry.getSummary())
            .map(summary -> new Remark().convert(summary))
            .map(summary -> "## [" + entry.getTitle() + "](" + entry.getLink() + ")\n ###### "
                + Entry.getFormattedAuthorUpdatedAt(entry.getAuthor(), entry.getUpdatedAt()) + "\n" + summary)
            .map(summary -> summary.replace("'", "\\'"))
            .subscribeOn(Schedulers.io()), summary -> summaryView.showMarkdown(entry.getLink(), summary));

        AwesomeBlogsApp.get().api().markAsRead(entry, System.currentTimeMillis());

        Analytics.event(Analytics.Event.VIEW_SUMMARY, new HashMap<String, String>(2) {{
            put(Analytics.Param.TITLE, entry.getTitle());
            put(Analytics.Param.LINK, entry.getLink());
        }});
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        onCloseAction.call();
    }

    @OnClick(R.id.more)
    void onMoreClick() {
        View menuView = LayoutInflater.from(context).inflate(R.layout.menu, bottomSheetView, false);
        menuView.findViewById(R.id.share).setOnClickListener($ -> {
            bottomSheetView.dismissSheet();
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, entry.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, link);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));

            Analytics.event(Analytics.Event.SHARE, new HashMap<String, String>(2) {{
                put(Analytics.Param.TITLE, entry.getTitle());
                put(Analytics.Param.LINK, link);
            }});
        });
        menuView.findViewById(R.id.open).setOnClickListener($ -> {
            bottomSheetView.dismissSheet();
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            } catch (ActivityNotFoundException e) {
                Alerts.show((Activity) context, R.string.error_title, R.string.error_invalid_link);
            }

            Analytics.event(Analytics.Event.OPEN_IN_BROWSER, new HashMap<String, String>(2) {{
                put(Analytics.Param.TITLE, entry.getTitle());
                put(Analytics.Param.LINK, link);
            }});
        });
        bottomSheetView.showWithSheetView(menuView);
        Analytics.event(Analytics.Event.MORE_MENU);
    }
}
