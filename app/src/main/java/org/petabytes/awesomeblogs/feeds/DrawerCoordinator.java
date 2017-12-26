package org.petabytes.awesomeblogs.feeds;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.favorite.FavoritesActivity;
import org.petabytes.awesomeblogs.history.HistoryActivity;
import org.petabytes.awesomeblogs.settings.SettingsActivity;
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
    @interface Category {}

    static final String ALL = "all";
    static final String DEVELOPER = "dev";
    static final String TECH_COMPANY = "company";
    static final String INSIGHTFUL = "insightful";

    @BindViews({R.id.all, R.id.developer, R.id.company, R.id.insightful})
    ViewGroup[] categoryViews;

    private final Context context;
    private final Action1<String> onCategorySelect;
    private final Preference<String> categoryPreference;

    DrawerCoordinator(@NonNull Context context, @NonNull Pair<Optional<String>, Integer> digestPair, @NonNull Action1<String> onCategorySelect) {
        this.context = context;
        this.onCategorySelect = onCategorySelect;
        this.categoryPreference = Preferences.category();
        digestPair.first.ifPresent(category -> {
            categoryPreference.set(category);
            Analytics.event(Analytics.Event.VIEW_DIGEST, Analytics.Param.SIZE, String.valueOf(digestPair.second));
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
    void onCategoryClick(@NonNull ViewGroup view) {
        selectView(view);
        categoryPreference.set(getCategory(view));
    }

    @OnClick(R.id.favorites)
    void onFavoritesClick() {
        context.startActivity(FavoritesActivity.intent(context));
    }

    @OnClick(R.id.history)
    void onHistoryClick() {
        context.startActivity(HistoryActivity.intent(context));
    }

    @OnClick(R.id.settings)
    void onSettingsClick() {
        context.startActivity(SettingsActivity.intent(context));
    }

    private void selectView(@NonNull ViewGroup view) {
        for (ViewGroup categoryView : categoryViews) {
            categoryView.setSelected(false);
            TextView textView = (TextView) categoryView.getChildAt(0);
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.nanum));
        }
        view.setSelected(true);
        TextView textView = (TextView) view.getChildAt(0);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
    }

    private ViewGroup getView(@Category String category) {
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
