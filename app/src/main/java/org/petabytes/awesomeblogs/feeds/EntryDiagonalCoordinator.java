package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.petabytes.api.model.Entry;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Dates;
import org.petabytes.awesomeblogs.web.WebViewActivity;
import org.petabytes.coordinator.Coordinator;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
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
        authorView1.setText("by " + entries.get(0).getAuthor() + "  /  " + Dates.getRelativeTimeString(
            Dates.getDefaultDateFormats().parse(entries.get(0).getUpdatedAt(), new ParsePosition(0)).getTime()));
        authorView2.setText("by " + entries.get(1).getAuthor() + "  /  " + Dates.getRelativeTimeString(
            Dates.getDefaultDateFormats().parse(entries.get(1).getUpdatedAt(), new ParsePosition(0)).getTime()));
    }

    @OnClick(R.id.top_container)
    void onTopContainerClick() {
        context.startActivity(WebViewActivity.intent(context, entries.get(0).getLink()));
    }

    @OnClick(R.id.bottom_container)
    void onBottomContainerClick() {
        context.startActivity(WebViewActivity.intent(context, entries.get(1).getLink()));
    }
}
