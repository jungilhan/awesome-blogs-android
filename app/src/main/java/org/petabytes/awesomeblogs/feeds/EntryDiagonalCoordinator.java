package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.petabytes.api.model.Entry;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.summary.SummaryActivity;
import org.petabytes.coordinator.Coordinator;

import java.text.ParsePosition;
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
        switch (new Random().nextInt(5)) {
            case 0: view.setBackgroundResource(R.drawable.background_diagonal_0); break;
            case 1: view.setBackgroundResource(R.drawable.background_diagonal_1); break;
            case 2: view.setBackgroundResource(R.drawable.background_diagonal_2); break;
            case 3: view.setBackgroundResource(R.drawable.background_diagonal_3); break;
            case 4: view.setBackgroundResource(R.drawable.background_diagonal_4); break;
        }
        titleView1.setText(entries.get(0).getTitle());
        titleView2.setText(entries.get(1).getTitle());
        authorView1.setText(Entry.getFormattedAuthorUpdatedAt(entries.get(0)));
        authorView2.setText(Entry.getFormattedAuthorUpdatedAt(entries.get(1)));
    }

    @OnClick(R.id.top_container)
    void onTopContainerClick() {
        Entry entry = entries.get(0);
        context.startActivity(SummaryActivity.intent(context,
            entry.getTitle(), Entry.getFormattedAuthorUpdatedAt(entry), entry.getSummary(), entry.getLink()));
    }

    @OnClick(R.id.bottom_container)
    void onBottomContainerClick() {
        Entry entry = entries.get(1);
        context.startActivity(SummaryActivity.intent(context,
            entry.getTitle(), Entry.getFormattedAuthorUpdatedAt(entries.get(0)), entry.getSummary(), entry.getLink()));
    }


}
