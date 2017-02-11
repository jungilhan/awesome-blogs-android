package org.petabytes.awesomeblogs.feeds;

import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.Activity;
import org.petabytes.coordinator.ActivityGraph;

public class FeedsActivity extends Activity {

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.feeds)
            .coordinator(R.id.bottom_sheet, new FeedsCoordinator(this))
            .build();
    }
}
