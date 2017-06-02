package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.awesomeblogs.search.SearchActivity;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Truss;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.ActivityGraph;

import butterknife.BindView;
import butterknife.OnClick;

import static org.petabytes.awesomeblogs.feeds.DrawerCoordinator.ALL;
import static org.petabytes.awesomeblogs.feeds.DrawerCoordinator.DEVELOPER;
import static org.petabytes.awesomeblogs.feeds.DrawerCoordinator.INSIGHTFUL;
import static org.petabytes.awesomeblogs.feeds.DrawerCoordinator.TECH_COMPANY;

public class FeedsActivity extends AwesomeActivity {

    private static String CATEGORY = "category";
    private static String FRESH_ENTRIES = "fresh_entries";

    @BindView(R.id.menu) ImageView menuButton;
    @BindView(R.id.sliding_menu) SlidingMenu slidingMenu;
    @BindView(R.id.category) TextView categoryView;
    @BindView(R.id.page) TextView pageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slidingMenu.setOnOpenedListener(() ->
            Analytics.event(Analytics.Event.OPEN_DRAWER));
    }

    @Override
    protected ActivityGraph createActivityGraph() {
        FeedsCoordinator feedsCoordinator;
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.main)
            .coordinator(R.id.bottom_sheet, feedsCoordinator = new FeedsCoordinator(this, (page, total, color) -> {
                menuButton.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                pageView.setText(new Truss()
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD))
                    .append(page + 1)
                    .popSpan()
                    .append("  /  ")
                    .append(total)
                    .build());
                Views.setVisible(pageView);
            }))
            .coordinator(R.id.drawer, new DrawerCoordinator(this, new Pair<>(getCategory(), getFreshEntries()), category -> {
                Views.setInvisible(pageView);
                feedsCoordinator.onCategorySelect(category);
                categoryView.setText(getCategoryText(category));
                slidingMenu.showContent();
            }))
            .build();
    }

    @OnClick(R.id.menu)
    void onDrawerButtonClick() {
        slidingMenu.showMenu();
    }

    @OnClick(R.id.search)
    void onSearchClick() {
        startActivity(SearchActivity.intent(this));
    }

    @OnClick(R.id.page)
    void onPageClick() {

    }

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing()) {
            slidingMenu.showContent();
        } else {
            super.onBackPressed();
        }
    }

    private Optional<String> getCategory() {
        return Optional.ofNullable(getIntent().getStringExtra(CATEGORY));
    }

    private int getFreshEntries() {
        return getIntent().getIntExtra(FRESH_ENTRIES, 0);
    }

    private static String getCategoryText(@DrawerCoordinator.Category String category) {
        switch (category) {
            case ALL:
                return "ALL";
            case DEVELOPER:
                return "DEVELOPER";
            case TECH_COMPANY:
                return "TECH COMPANY";
            case INSIGHTFUL:
                return "INSIGHTFUL";
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Intent intent(@NonNull Context context) {
        Intent intent = new Intent(context, FeedsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static Intent intent(@NonNull Context context, @NonNull String category) {
        Intent intent = intent(context);
        intent.putExtra(CATEGORY, category);
        return intent;
    }

    public static Intent intent(@NonNull Context context, @NonNull String category, int freshEntries) {
        Intent intent = intent(context, category);
        intent.putExtra(FRESH_ENTRIES, freshEntries);
        return intent;
    }
}
