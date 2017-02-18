package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.View;
import android.widget.TextView;

import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.Coordinator;

import java.lang.annotation.Retention;

import butterknife.BindViews;
import butterknife.OnClick;
import rx.functions.Action1;

import static java.lang.annotation.RetentionPolicy.SOURCE;

class DrawerCoordinator extends Coordinator {

    @Retention(SOURCE)
    @StringDef({ ALL, DEVELOPER, TECH_COMPANY, INSIGHTFUL})
    @interface Category {}
    static final String ALL = "all";
    static final String DEVELOPER = "dev";
    static final String TECH_COMPANY = "company";
    static final String INSIGHTFUL = "insightful";

    @BindViews({R.id.all, R.id.developer, R.id.tech, R.id.insightful})
    TextView[] categoryViews;

    private final Context context;
    private final Action1<String> onCategorySelect;
    private final Preference<String> categoryPreference;

    DrawerCoordinator(@NonNull Context context, @NonNull Action1<String> onCategorySelect) {
        this.context = context;
        this.onCategorySelect = onCategorySelect;
        this.categoryPreference = AwesomeBlogsApp.get().preferences().getString("category", ALL);
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        @Category String category = categoryPreference.get();
        selectView(getView(category));
        bind(categoryPreference.asObservable(), onCategorySelect);
    }

    @OnClick({R.id.all, R.id.developer, R.id.tech, R.id.insightful})
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

    private @Category String getCategory(@NonNull View view) {
        switch (view.getId()) {
            case R.id.all:
                return ALL;
            case R.id.developer:
                return DEVELOPER;
            case R.id.tech:
                return TECH_COMPANY;
            case R.id.insightful:
                return INSIGHTFUL;
        }
        throw new IllegalArgumentException("Invalid view");
    }
}
