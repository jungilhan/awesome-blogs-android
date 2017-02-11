package org.petabytes.awesomeblogs.web;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.Activity;
import org.petabytes.coordinator.ActivityGraph;

import hugo.weaving.DebugLog;

public class WebViewActivity extends Activity {

    private static final String URL = "url";

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.web)
            .coordinator(R.id.bottom_sheet, new WebViewCoordinator(getIntent().getStringExtra(URL), this::finish))
            .build();
    }

    @DebugLog
    @Override
    public void onBackPressed() {
        WebViewCoordinator webViewCoordinator = (WebViewCoordinator) getActivityGraph().getCoordinatorMap().get(R.id.bottom_sheet);
        if (!webViewCoordinator.historyBackIfNeeded()) {
            super.onBackPressed();
        }
    }

    public static Intent intent(@NonNull Context context, @NonNull String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL, url);
        return intent;
    }
}
