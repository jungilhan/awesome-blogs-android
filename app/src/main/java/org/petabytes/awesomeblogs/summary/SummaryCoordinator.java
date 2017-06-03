package org.petabytes.awesomeblogs.summary;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.overzealous.remark.Remark;

import org.petabytes.api.source.local.Entry;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.chrome.Chromes;
import org.petabytes.awesomeblogs.util.Alerts;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Intents;
import org.petabytes.awesomeblogs.util.Strings;
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

class SummaryCoordinator extends Coordinator {

    @BindView(R.id.bottom_sheet) BottomSheetLayout bottomSheetView;
    @BindView(R.id.summary) MarkdownView summaryView;
    @BindView(R.id.favorite) ImageView favoriteButton;

    private final Context context;
    private final String link;
    private final String from;
    private final Action1<Integer> onLoading;
    private final Action0 onCloseAction;
    private Entry entry;

    SummaryCoordinator(@NonNull Context context, @NonNull String link, @NonNull String from,
                       @NonNull Action1<Integer> onLoadingAction, @NonNull Action0 onCloseAction) {
        this.context = context;
        this.link = link;
        this.from = from;
        this.onLoading = onLoadingAction;
        this.onCloseAction = onCloseAction;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        bind(AwesomeBlogsApp.get().api().getEntry(link)
            .doOnNext(entry -> this.entry = entry), this::onEntryChanged);
        bind(AwesomeBlogsApp.get().api().isFavorite(link), isFavorite ->
            favoriteButton.setImageResource(isFavorite ? R.drawable.favorite : R.drawable.favorite_outline));

        summaryView.setOnProgressChangedListener(onLoading::call);
        summaryView.setOnOverrideUrlAction(
            url -> {
                Chromes.open(context, url);
                Analytics.event(Analytics.Event.OPEN_IN_BROWSER, new HashMap<String, String>(2) {{
                    put(Analytics.Param.TITLE, entry.getTitle());
                    put(Analytics.Param.LINK, url);
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

        Analytics.event(Analytics.Event.VIEW_SUMMARY, new HashMap<String, String>(3) {{
            put(Analytics.Param.TITLE, entry.getTitle());
            put(Analytics.Param.LINK, entry.getLink());
            put(Analytics.Param.FROM, from);
        }});
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        onCloseAction.call();
    }

    @OnClick(R.id.favorite)
    void onFavoriteClick() {
        bind(AwesomeBlogsApp.get().api().isFavorite(link).first(), isFavorite -> {
            if (isFavorite) {
                AwesomeBlogsApp.get().api().unMarkAsFavorite(entry);
            } else {
                AwesomeBlogsApp.get().api().markAsFavorite(entry, System.currentTimeMillis());
            }
        });
    }

    @OnClick(R.id.share)
    void onShareClick() {
        context.startActivity(Intent.createChooser(Intents.createShareIntent(entry.getTitle(), link), context.getString(R.string.share)));
        Analytics.event(Analytics.Event.SHARE, new HashMap<String, String>(2) {{
            put(Analytics.Param.TITLE, entry.getTitle());
            put(Analytics.Param.LINK, link);
        }});
    }

    @OnClick(R.id.copy)
    void onCopyClick() {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(Strings.EMPTY, link);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, R.string.copy_link_completed, Toast.LENGTH_SHORT).show();
        Analytics.event(Analytics.Event.COPY_LINK, Analytics.Param.LINK, link);
    }

    @OnClick(R.id.open)
    void onOpenClick() {
        try {
            Chromes.open(context, link);
        } catch (ActivityNotFoundException e) {
            Alerts.show((Activity) context, R.string.error_title, R.string.error_invalid_link);
        }
        Analytics.event(Analytics.Event.OPEN_IN_BROWSER, new HashMap<String, String>(2) {{
            put(Analytics.Param.TITLE, entry.getTitle());
            put(Analytics.Param.LINK, link);
        }});
    }
}
