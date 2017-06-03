package org.petabytes.awesomeblogs.favorite;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.coordinator.ActivityGraph;

public class FavoritesActivity extends AwesomeActivity {

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.favorites)
            .coordinator(R.id.bottom_sheet, new FavoritesCoordinator(this, this::finish))
            .build();
    }

    public static Intent intent(@NonNull Context context) {
        return new Intent(context, FavoritesActivity.class);
    }
}
