package org.petabytes.awesomeblogs.feeds;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.Activity;
import org.petabytes.coordinator.ActivityGraph;

import butterknife.BindView;
import rx.functions.Action1;

public class FeedsActivity extends Activity {

    @BindView(R.id.sliding_menu) SlidingMenu slidingMenu;

    @Override
    protected ActivityGraph createActivityGraph() {
        FeedsCoordinator feedsCoordinator;
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.main)
            .coordinator(R.id.bottom_sheet, feedsCoordinator = new FeedsCoordinator(this))
            .coordinator(R.id.drawer, new DrawerCoordinator(this, category -> {
                feedsCoordinator.onCategorySelect(category);
                slidingMenu.showContent();
            }))
            .build();
    }
}
