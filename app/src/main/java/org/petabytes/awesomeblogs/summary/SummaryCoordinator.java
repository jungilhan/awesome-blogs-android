package org.petabytes.awesomeblogs.summary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.overzealous.remark.Remark;

import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.Coordinator;

import butterknife.BindView;
import butterknife.OnClick;
import eu.fiskur.markdownview.MarkdownView;
import rx.Observable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

class SummaryCoordinator extends Coordinator {

    @BindView(R.id.summary) MarkdownView summaryView;

    private final Context context;
    private final String authorUpdatedAt;
    private final String title;
    private final String summary;
    private final String link;
    private final Action0 onCloseAction;

    SummaryCoordinator(@NonNull Context context, @NonNull String title, @NonNull String authorUpdatedAt,
                       @NonNull String summary, @NonNull String link, @NonNull Action0 onCloseAction) {
        this.context = context;
        this.title = title;
        this.authorUpdatedAt = authorUpdatedAt;
        this.summary = summary;
        this.link = link;
        this.onCloseAction = onCloseAction;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        bind(Observable.just(summary)
            .map(summary -> new Remark().convert(summary))
            .map(summary -> "## [" + title + "](" + link + ")\n ###### " + authorUpdatedAt + "\n" + summary)
            .map(summary -> summary.replace("'", "\\'"))
            .subscribeOn(Schedulers.io()), summary -> summaryView.showMarkdown(summary));
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        onCloseAction.call();
    }

    @OnClick(R.id.open)
    void onOpenInBrowserClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        context.startActivity(intent);
    }

    @OnClick(R.id.share)
    void onShareClick() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, link);
        context.startActivity(Intent.createChooser(intent, "Share..."));
    }

    @OnClick(R.id.more)
    void onMoreClick() {
        Toast.makeText(context, "구현 중...", Toast.LENGTH_SHORT).show();
    }
}
