package org.petabytes.awesomeblogs.feeds;

import android.graphics.PorterDuff;
import android.widget.ImageView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.Activity;
import org.petabytes.coordinator.ActivityGraph;

import butterknife.BindView;
import butterknife.OnClick;

public class FeedsActivity extends Activity {

    @BindView(R.id.menu) ImageView menuButton;
    @BindView(R.id.sliding_menu) SlidingMenu slidingMenu;

    @Override
    protected ActivityGraph createActivityGraph() {
        FeedsCoordinator feedsCoordinator;
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.main)
            .coordinator(R.id.bottom_sheet, feedsCoordinator = new FeedsCoordinator(this, color ->
                menuButton.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)))
            .coordinator(R.id.drawer, new DrawerCoordinator(this, category -> {
                feedsCoordinator.onCategorySelect(category);
                slidingMenu.showContent();
            }))
            .build();
    }

    @OnClick(R.id.menu)
    void onDrawerButtonClick() {
        slidingMenu.showMenu();
    }
}
