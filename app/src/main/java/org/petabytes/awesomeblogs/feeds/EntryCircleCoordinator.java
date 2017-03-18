package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.petabytes.api.source.local.Entry;
import org.petabytes.api.util.Dates;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.summary.SummaryActivity;
import org.petabytes.awesomeblogs.widget.CircleView;
import org.petabytes.coordinator.Coordinator;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

class EntryCircleCoordinator extends Coordinator {

    @BindView(R.id.circle) CircleView circleView;
    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.author) TextView authorView;
    @BindView(R.id.date) TextView dateView;

    private final Context context;
    private final Entry entry;

    EntryCircleCoordinator(@NonNull Context context, @NonNull Entry entry) {
        this.context = context;
        this.entry = entry;
    }

    @Override
    public void attach(View view) {
        super.attach(view);
        bind(AwesomeBlogsApp.get().api()
            .isRead(entry.getLink()), isRead -> {
                titleView.setText(entry.getTitle());
                titleView.setAlpha(isRead ? 0.65f : 1f);
                authorView.setText("by " + entry.getAuthor());
                dateView.setText(Dates.getRelativeTimeString(
                    Dates.getDefaultDateFormats().parseDateTime(entry.getUpdatedAt()).getMillis()));
            });
        setFillColor();
    }

    @OnClick(R.id.circle)
    void onCircleClick() {
        context.startActivity(SummaryActivity.intent(context, entry.getLink()));
    }

    private void setFillColor() {
        switch (new Random(System.identityHashCode(entry.getTitle())).nextInt(12)) {
            case 0: circleView.setFillColor(R.color.background_2); break;
            case 1: circleView.setFillColor(R.color.background_3); break;
            case 2: circleView.setFillColor(R.color.background_4); break;
            case 3: circleView.setFillColor(R.color.background_5); break;
            case 4: circleView.setFillColor(R.color.background_6); break;
            case 5: circleView.setFillColor(R.color.background_7); break;
            case 6: circleView.setFillColor(R.color.background_8); break;
            case 7: circleView.setFillColor(R.color.background_9); break;
            case 8: circleView.setFillColor(R.color.background_11); break;
            case 9: circleView.setFillColor(R.color.background_13); break;
            case 10: circleView.setFillColor(R.color.background_14); break;
            case 11: circleView.setFillColor(R.color.background_15); break;
        }
    }

}
