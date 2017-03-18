package org.petabytes.awesomeblogs;

import android.text.TextUtils;

import com.f2prateek.rx.preferences.Preference;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import org.petabytes.api.Api;
import org.petabytes.awesomeblogs.util.Devices;
import org.petabytes.awesomeblogs.util.Preferences;
import org.petabytes.coordinator.ActivityLayoutBinder;

import timber.log.Timber;

public class DebugAwesomeBlogsApp extends AwesomeBlogsApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                .build());
    }

    @Override
    protected ActivityLayoutBinder createActivityLayoutBinder() {
        return new DebugActivityLayoutBinder();
    }

    @Override
    protected Api createApi() {
        return new Api(this,
            () -> "awesome-blogs-android/" + BuildConfig.VERSION_NAME,
            () -> {
                Preference<String> preference = Preferences.deviceId();
                String deviceId = preference.get();
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = Devices.getId(this);
                    preference.set(deviceId);
                }
                return deviceId;
            },
            () -> Preferences.fcmToken().get(),
            () -> Preferences.accessToken().get(),
            true);
    }
}
