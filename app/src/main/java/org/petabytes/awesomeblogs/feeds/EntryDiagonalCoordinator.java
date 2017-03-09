package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.petabytes.api.source.local.Entry;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.summary.SummaryActivity;
import org.petabytes.coordinator.Coordinator;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

class EntryDiagonalCoordinator extends Coordinator {

    @BindView(R.id.title_1) TextView titleView1;
    @BindView(R.id.title_2) TextView titleView2;
    @BindView(R.id.author_1) TextView authorView1;
    @BindView(R.id.author_2) TextView authorView2;

    private final Context context;
    private final List<Entry> entries;

    EntryDiagonalCoordinator(@NonNull Context context, @NonNull List<Entry> entries) {
        this.context = context;
        this.entries = entries;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        bind(AwesomeBlogsApp.get().api()
            .isRead(entries.get(0).getLink()), isRead -> {
                titleView1.setText(entries.get(0).getTitle());
                titleView1.setAlpha(isRead ? 0.35f : 1f);
                authorView1.setText(Entry.getFormattedAuthorUpdatedAt(entries.get(0)));
            });
        bind(AwesomeBlogsApp.get().api()
            .isRead(entries.get(1).getLink()), isRead -> {
                titleView2.setText(entries.get(1).getTitle());
                titleView2.setAlpha(isRead ? 0.65f : 1f);
                authorView2.setText(Entry.getFormattedAuthorUpdatedAt(entries.get(1)));
            });
        setBackground(view);
    }

    @OnClick(R.id.top_container)
    void onTopContainerClick() {
        Entry entry = entries.get(0);
        context.startActivity(SummaryActivity.intent(context, entry.getLink()));
    }

    @OnClick(R.id.bottom_container)
    void onBottomContainerClick() {
        Entry entry = entries.get(1);
        context.startActivity(SummaryActivity.intent(context, entry.getLink()));
    }

    private void setBackground(@NonNull View view) {
        switch (new Random().nextInt(12)) {
            case 0: view.setBackgroundResource(R.drawable.background_diagonal_0); break;
            case 1: view.setBackgroundResource(R.drawable.background_diagonal_1); break;
            case 2: view.setBackgroundResource(R.drawable.background_diagonal_2); break;
            case 3: view.setBackgroundResource(R.drawable.background_diagonal_3); break;
            case 4: view.setBackgroundResource(R.drawable.background_diagonal_4); break;
            case 5: view.setBackgroundResource(R.drawable.background_diagonal_5); break;
            case 6: view.setBackgroundResource(R.drawable.background_diagonal_6); break;
            case 7: view.setBackgroundResource(R.drawable.background_diagonal_7); break;
            case 8: view.setBackgroundResource(R.drawable.background_diagonal_8); break;
            case 9: view.setBackgroundResource(R.drawable.background_diagonal_9); break;
            case 10: view.setBackgroundResource(R.drawable.background_diagonal_10); break;
            case 11: view.setBackgroundResource(R.drawable.background_diagonal_11); break;
        }
    }
}
