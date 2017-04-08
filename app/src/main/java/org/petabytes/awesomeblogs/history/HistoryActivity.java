package org.petabytes.awesomeblogs.history;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.coordinator.ActivityGraph;

public class HistoryActivity extends AwesomeActivity {

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.history)
            .coordinator(R.id.bottom_sheet, new HistoryCoordinator(this, this::finish))
            .build();
    }

    public static Intent intent(@NonNull Context context) {
        return new Intent(context, HistoryActivity.class);
    }
}
