package org.petabytes.awesomeblogs.feeds;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Preferences;
import org.petabytes.coordinator.Coordinator;

import java.lang.annotation.Retention;

import butterknife.BindViews;
import butterknife.OnClick;
import rx.functions.Action1;

import static java.lang.annotation.RetentionPolicy.SOURCE;

class DrawerCoordinator extends Coordinator {

    @Retention(SOURCE)
    @StringDef({ALL, DEVELOPER, TECH_COMPANY, INSIGHTFUL})
    @interface Category {
    }

    static final String ALL = "all";
    static final String DEVELOPER = "dev";
    static final String TECH_COMPANY = "company";
    static final String INSIGHTFUL = "insightful";

    @BindViews({R.id.all, R.id.developer, R.id.company, R.id.insightful})
    TextView[] categoryViews;

    private final Action1<String> onCategorySelect;
    private final Preference<String> categoryPreference;

    DrawerCoordinator(@NonNull Optional<String> category, @NonNull Action1<String> onCategorySelect) {
        this.onCategorySelect = onCategorySelect;
        this.categoryPreference = Preferences.category();
        category.ifPresent(c -> {
            categoryPreference.set(c);
            Analytics.event(Analytics.Event.VIEW_DIGEST);
        });
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        @Category String category = categoryPreference.get();
        selectView(getView(category));
        bind(categoryPreference.asObservable(), onCategorySelect);
    }

    @OnClick({R.id.all, R.id.developer, R.id.company, R.id.insightful})
    void onCategoryClick(@NonNull TextView view) {
        selectView(view);
        categoryPreference.set(getCategory(view));
    }

    private void selectView(@NonNull TextView view) {
        for (TextView categoryView : categoryViews) {
            categoryView.setSelected(false);
            categoryView.setTypeface(categoryView.getTypeface(), Typeface.NORMAL);
        }
        view.setSelected(true);
        view.setTypeface(view.getTypeface(), Typeface.BOLD);
    }

    private TextView getView(@Category String category) {
        switch (category) {
            case ALL:
                return categoryViews[0];
            case DEVELOPER:
                return categoryViews[1];
            case TECH_COMPANY:
                return categoryViews[2];
            case INSIGHTFUL:
                return categoryViews[3];
        }
        throw new IllegalArgumentException("Invalid category");
    }

    @Category
    private String getCategory(@NonNull View view) {
        switch (view.getId()) {
            case R.id.all:
                Analytics.event(Analytics.Event.VIEW_ALL);
                return ALL;
            case R.id.developer:
                Analytics.event(Analytics.Event.VIEW_DEVELOPER);
                return DEVELOPER;
            case R.id.company:
                Analytics.event(Analytics.Event.VIEW_TECH_COMPANY);
                return TECH_COMPANY;
            case R.id.insightful:
                Analytics.event(Analytics.Event.VIEW_INSIGHTFUL);
                return INSIGHTFUL;
        }
        throw new IllegalArgumentException("Invalid view");
    }
}
