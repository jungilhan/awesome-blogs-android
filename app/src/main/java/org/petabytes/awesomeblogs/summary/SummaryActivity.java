package org.petabytes.awesomeblogs.summary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Strings;
import org.petabytes.coordinator.Activity;
import org.petabytes.coordinator.ActivityGraph;

public class SummaryActivity extends Activity {

    private static final String TITLE = "title";
    private static final String AUTHOR_UPDATED_AT = "authorUpdatedAt";
    private static final String SUMMARY = "summary";
    private static final String LINK = "link";

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.summary)
            .coordinator(R.id.bottom_sheet, new SummaryCoordinator(this,
                getStringExtra(TITLE), getStringExtra(AUTHOR_UPDATED_AT), getStringExtra(SUMMARY), getStringExtra(LINK),
                this::finish))
            .build();
    }

    public String getStringExtra(String name) {
        String extra = getIntent().getStringExtra(name);
        return extra != null ? extra : Strings.EMPTY;
    }

    public static Intent intent(@NonNull Context context, @NonNull String title,
                                @NonNull String authorUpdatedAt, @NonNull String summary, @NonNull String link) {
        Intent intent = new Intent(context, SummaryActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(AUTHOR_UPDATED_AT, authorUpdatedAt);
        intent.putExtra(SUMMARY, summary);
        intent.putExtra(LINK, link);
        return intent;
    }
}
