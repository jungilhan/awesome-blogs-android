package org.petabytes.awesomeblogs.feeds;

import android.graphics.PorterDuff;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.awesomeblogs.util.Truss;
import org.petabytes.coordinator.ActivityGraph;

import butterknife.BindView;
import butterknife.OnClick;

public class FeedsActivity extends AwesomeActivity {

    @BindView(R.id.menu) ImageView menuButton;
    @BindView(R.id.sliding_menu) SlidingMenu slidingMenu;
    @BindView(R.id.page) TextView pageView;

    @Override
    protected ActivityGraph createActivityGraph() {
        FeedsCoordinator feedsCoordinator;
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.main)
            .coordinator(R.id.bottom_sheet, feedsCoordinator = new FeedsCoordinator(this, (page, total, color) -> {
                menuButton.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                pageView.setTextColor(color);
                pageView.setText(new Truss()
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD))
                    .append(page + 1)
                    .popSpan()
                    .append("  /  ")
                    .append(total)
                    .build());
            }))
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

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing()) {
            slidingMenu.showContent();
        } else {
            super.onBackPressed();
        }
    }
}
