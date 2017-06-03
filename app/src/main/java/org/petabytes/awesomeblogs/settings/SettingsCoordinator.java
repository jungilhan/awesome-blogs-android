package org.petabytes.awesomeblogs.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.BuildConfig;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Intents;
import org.petabytes.awesomeblogs.util.Preferences;
import org.petabytes.coordinator.Coordinator;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action0;

class SettingsCoordinator extends Coordinator {

    @BindView(R.id.digest_switch) SwitchCompat digestSwitch;
    @BindView(R.id.silent_switch) SwitchCompat silentSwitch;
    @BindView(R.id.version) TextView versionView;

    @NonNull private final Context context;
    private final Action0 closeAction;
    private final Preference<Boolean> digestPreference;
    private final Preference<Boolean> silentPreference;

    SettingsCoordinator(@NonNull Context context, @NonNull Action0 closeAction) {
        this.context = context;
        this.closeAction = closeAction;
        this.digestPreference = Preferences.digest();
        this.silentPreference = Preferences.silent();
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        bind(digestPreference.asObservable()
            .doOnNext($ -> digestSwitch.animate().setStartDelay(100).alpha(1)), digestSwitch::setChecked);
        bind(silentPreference.asObservable()
            .doOnNext($ -> silentSwitch.animate().setStartDelay(100).alpha(1)), silentSwitch::setChecked);
        versionView.setText(context.getString(R.string.settings_version, BuildConfig.VERSION_NAME));
        Analytics.event(Analytics.Event.VIEW_SETTINGS);
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        closeAction.call();
    }

    @OnClick({R.id.digest, R.id.digest_switch})
    void onDigestClick() {
        digestPreference.set(!digestPreference.get());
        Analytics.event(Analytics.Event.SETTINGS_DIGEST,
            Analytics.Param.ENABLED, String.valueOf(digestPreference.get()));
    }

    @OnClick({R.id.silent, R.id.silent_switch})
    void onSilentClick() {
        silentPreference.set(!silentPreference.get());
        Analytics.event(Analytics.Event.SETTINGS_SILENT,
            Analytics.Param.ENABLED, String.valueOf(silentPreference.get()));
    }

    @OnClick(R.id.store)
    void onStoreClick() {
        context.startActivity(Intents.createStoreIntent());
    }

    @OnClick(R.id.github)
    void onGitHubClick() {
        context.startActivity(Intents.createGitHubIntent());
    }
}
