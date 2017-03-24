package org.petabytes.awesomeblogs.summary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.awesomeblogs.util.Strings;
import org.petabytes.coordinator.ActivityGraph;

import hugo.weaving.DebugLog;

public class SummaryActivity extends AwesomeActivity {

    private static final String LINK = "link";

    private FooterCoordinator footerCoordinator;

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.summary)
            .coordinator(R.id.bottom_sheet, new SummaryCoordinator(this, getStringExtra(LINK), this::onLoadingComplete, this::finish))
            .coordinator(R.id.footer, footerCoordinator = new FooterCoordinator(this, getStringExtra(LINK)))
            .build();
    }

    @DebugLog
    private void onLoadingComplete() {
        footerCoordinator.show();
    }

    public String getStringExtra(@NonNull String name) {
        String extra = getIntent().getStringExtra(name);
        return extra != null ? extra : Strings.EMPTY;
    }

    public static Intent intent(@NonNull Context context, @NonNull String link) {
        Intent intent = new Intent(context, SummaryActivity.class);
        intent.putExtra(LINK, link);
        return intent;
    }
}
