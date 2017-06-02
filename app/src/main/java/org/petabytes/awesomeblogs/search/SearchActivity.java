package org.petabytes.awesomeblogs.search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.coordinator.ActivityGraph;

public class SearchActivity extends AwesomeActivity {

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.search)
            .coordinator(R.id.container, new SearchCoordinator(this, this::finish))
            .build();
    }

    public static Intent intent(@NonNull Context context) {
        return new Intent(context, SearchActivity.class);
    }
}
