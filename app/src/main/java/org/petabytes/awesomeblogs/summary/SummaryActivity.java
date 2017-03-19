package org.petabytes.awesomeblogs.summary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.awesomeblogs.util.Strings;
import org.petabytes.coordinator.ActivityGraph;

public class SummaryActivity extends AwesomeActivity {

    private static final String LINK = "link";

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.summary)
            .coordinator(R.id.bottom_sheet, new SummaryCoordinator(this, getStringExtra(LINK), this::finish))
            .build();
    }

    public String getStringExtra(String name) {
        String extra = getIntent().getStringExtra(name);
        return extra != null ? extra : Strings.EMPTY;
    }

    public static Intent intent(@NonNull Context context, @NonNull String link) {
        Intent intent = new Intent(context, SummaryActivity.class);
        intent.putExtra(LINK, link);
        return intent;
    }
}
