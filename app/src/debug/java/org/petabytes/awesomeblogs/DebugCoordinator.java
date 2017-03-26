package org.petabytes.awesomeblogs;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.digest.Schedulers;
import org.petabytes.awesomeblogs.util.Preferences;
import org.petabytes.coordinator.Coordinator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.subjects.BehaviorSubject;

class DebugCoordinator extends Coordinator {

    @BindView(R.id.name) TextView nameView;
    @BindView(R.id.email) TextView emailView;
    @BindView(R.id.uid) TextView uidView;
    @BindView(R.id.fcm_token) TextView fcmTokenView;
    @BindView(R.id.access_token) TextView accessTokenView;
    @BindView(R.id.device_id) TextView deviceIdView;
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
    @BindView(R.id.morning) TextView morningView;
    @BindView(R.id.evening) TextView eveningView;
    @BindView(R.id.all) TextView allView;
    @BindView(R.id.developer) TextView developerView;
    @BindView(R.id.company) TextView companyView;
    @BindView(R.id.insightful) TextView insightfulView;

    private final Context context;
    private final BehaviorSubject<Boolean> drawerOpenedSubject;

    DebugCoordinator(@NonNull Context context, @NonNull DrawerLayout drawerLayout) {
        this.context = context;
        this.drawerOpenedSubject = BehaviorSubject.create();
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerOpenedSubject.onNext(true);
            }
        });
    }

    @Override
    public void attach(View view) {
        super.attach(view);
        initAccountSection();
        initFcmSection();
        initAppSection();
        initDeviceSection();
        initDigestSection();
        initExpiryDatesSection();
    }

    private void initAccountSection() {
        AwesomeBlogsApp.get().authenticator()
            .isSignIn()
            .filter(isSignIn -> isSignIn)
            .flatMap($ -> AwesomeBlogsApp.get().authenticator().user())
            .subscribe(optional -> {
                optional.ifPresent(user -> {
                    nameView.setText(user.getName());
                    emailView.setText(user.getEmail());
                    uidView.setText(user.getId());
                });
            });
    }

    private void initFcmSection() {
        bind(Preferences.fcmToken().asObservable()
            .filter(token -> !TextUtils.isEmpty(token)), fcmTokenView::setText);
        bind(Preferences.accessToken().asObservable()
            .filter(token -> !TextUtils.isEmpty(token)), accessTokenView::setText);
        bind(Preferences.deviceId().asObservable()
            .filter(token -> !TextUtils.isEmpty(token)), deviceIdView::setText);
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

    private void initDigestSection() {
        Preference<Long> morningDigestPreference = Preferences.morningDigestAt();
        Preference<Long> eveningDigestPreference = Preferences.eveningDigestAt();

        bind(morningDigestPreference.asObservable(),
            digestAt -> morningView.setText(new SimpleDateFormat("dd日 HH:mm:ss", Locale.getDefault()).format(new Date(digestAt))));
        bind(eveningDigestPreference.asObservable(),
            digestAt -> eveningView.setText(new SimpleDateFormat("dd日 HH:mm:ss", Locale.getDefault()).format(new Date(digestAt))));

        morningView.setOnClickListener($ -> {
            long digestAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
            morningDigestPreference.set(digestAt);
            Schedulers.set(context, digestAt, Schedulers.MORNING);
        });
        eveningView.setOnClickListener($ -> {
            long digestAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
            eveningDigestPreference.set(digestAt);
            Schedulers.set(context, digestAt, Schedulers.EVENING);
        });
    }

    private void initExpiryDatesSection() {
        bind(getExpiryDate("all"), allView::setText);
        bind(getExpiryDate("dev"), developerView::setText);
        bind(getExpiryDate("company"), companyView::setText);
        bind(getExpiryDate("insightful"), insightfulView::setText);

        allView.setOnClickListener($ -> AwesomeBlogsApp.get().api().clearExpiryDate("all"));
        developerView.setOnClickListener($ -> AwesomeBlogsApp.get().api().clearExpiryDate("dev"));
        companyView.setOnClickListener($ -> AwesomeBlogsApp.get().api().clearExpiryDate("company"));
        insightfulView.setOnClickListener($ -> AwesomeBlogsApp.get().api().clearExpiryDate("insightful"));
    }

    @OnClick(R.id.uid)
    void onUidClick() {
        AwesomeBlogsApp.get().authenticator().signOut();
    }

    private Observable<String> getExpiryDate(@NonNull String category) {
        return Observable.combineLatest(drawerOpenedSubject, AwesomeBlogsApp.get().api().getExpiryDate(category),
            ($, date) -> date.getTime() > System.currentTimeMillis()
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
