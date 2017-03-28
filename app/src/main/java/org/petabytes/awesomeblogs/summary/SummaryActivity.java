package org.petabytes.awesomeblogs.summary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Strings;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.ActivityGraph;

import butterknife.BindView;

public class SummaryActivity extends AwesomeActivity {

    private static final String LINK = "link";
    private static final String FROM = "from";

    @BindView(R.id.progress_bar) ProgressBar progressBar;

    private FooterCoordinator footerCoordinator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar.getProgressDrawable().setColorFilter(
            Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.summary)
            .coordinator(R.id.bottom_sheet, new SummaryCoordinator(
                this, getStringExtra(LINK), getStringExtra(FROM), this::onLoading, this::finish))
            .coordinator(R.id.footer, footerCoordinator = new FooterCoordinator(this, getStringExtra(LINK)))
            .build();
    }

    private void onLoading(int progress) {
        progressBar.setProgress(progress);
        if (progress == 100) {
            progressBar.animate().alpha(0f)
                .withEndAction(() -> Views.setGone(progressBar));
        }
        footerCoordinator.show(progress);
    }

    public String getStringExtra(@NonNull String name) {
        String extra = getIntent().getStringExtra(name);
        return extra != null ? extra : Strings.EMPTY;
    }

    public static Intent intent(@NonNull Context context, @NonNull String link) {
        return intent(context, link, Analytics.Param.FEEDS);
    }

    public static Intent intent(@NonNull Context context, @NonNull String link, @NonNull String from) {
        Intent intent = new Intent(context, SummaryActivity.class);
        intent.putExtra(LINK, link);
        intent.putExtra(FROM, from);
        return intent;
    }
}
