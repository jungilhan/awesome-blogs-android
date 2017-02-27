package com.petabytes.awesomeblogs;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.BuildConfig;
import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.Coordinator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;

class DebugCoordinator extends Coordinator {

    @BindView(R.id.git_sha) TextView gitShaView;
    @BindView(R.id.build_date) TextView buildDateView;
    @BindView(R.id.build_type) TextView buildTypeView;
    @BindView(R.id.build_flavor) TextView buildFlavorView;
    @BindView(R.id.version) TextView versionView;
    @BindView(R.id.make) TextView makeView;
    @BindView(R.id.model) TextView modelView;
    @BindView(R.id.resolution) TextView resolutionView;
    @BindView(R.id.density) TextView densityView;
    @BindView(R.id.release) TextView releaseView;
    @BindView(R.id.api) TextView apiView;
    @BindView(R.id.all) TextView allView;
    @BindView(R.id.developer) TextView developerView;
    @BindView(R.id.company) TextView companyView;
    @BindView(R.id.insightful) TextView insightfulView;

    private final Context context;

    DebugCoordinator(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void attach(View view) {
        super.attach(view);
        initAppSection();
        initDeviceSection();
        initExpiryDates();
    }

    private void initAppSection() {
        gitShaView.setText(BuildConfig.GIT_SHA);
        buildDateView.setText(new SimpleDateFormat("yy.MM.dd HH:mm", Locale.getDefault())
            .format(new Date(TimeUnit.SECONDS.toMillis(BuildConfig.GIT_TIMESTAMP))));
        buildTypeView.setText(BuildConfig.BUILD_TYPE);
        buildFlavorView.setText(BuildConfig.FLAVOR);
        versionView.setText(BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")");
    }

    private void initDeviceSection() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        String densityBucket = getDensityString(displayMetrics);
        makeView.setText(Build.MANUFACTURER);
        modelView.setText(Build.MODEL);
        resolutionView.setText(displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);
        densityView.setText(displayMetrics.densityDpi + "dpi (" + densityBucket + ")");
        releaseView.setText(Build.VERSION.RELEASE);
        apiView.setText(String.valueOf(Build.VERSION.SDK_INT));
    }

    private void initExpiryDates() {
        getView().postDelayed(() -> {
            bind(getExpiryDate("all"), allView::setText);
            bind(getExpiryDate("dev"), developerView::setText);
            bind(getExpiryDate("company"), companyView::setText);
            bind(getExpiryDate("insightful"), insightfulView::setText);
        }, 500);
    }

    private Observable<String> getExpiryDate(@NonNull String category) {
        return AwesomeBlogsApp.get().api()
            .getExpiryDate(category)
            .map(date -> date.getTime() > System.currentTimeMillis()
                ? new SimpleDateFormat("dd日 HH:mm:ss", Locale.getDefault()).format(date) : "Expired");
    }

    private static String getDensityString(@NonNull DisplayMetrics displayMetrics) {
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return String.valueOf(displayMetrics.densityDpi);
        }
    }

}
