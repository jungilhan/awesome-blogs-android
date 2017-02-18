package org.petabytes.awesomeblogs;

import android.app.Application;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import io.fabric.sdk.android.Fabric;
import org.petabytes.api.Api;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AwesomeBlogsApp extends Application {

    private static AwesomeBlogsApp instance;
    private Api api;
    private RxSharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/NanumBarunGothicLight.otf")
            .setFontAttrId(R.attr.fontPath)
            .build());
    }

    public static AwesomeBlogsApp get() {
        return instance;
    }

    public Api api() {
        return api == null ? api = createApi() : api;
    }

    protected Api createApi() {
        return new Api(this, false);
    }

    public RxSharedPreferences preferences() {
        return preferences == null ? preferences = createPreferences() : preferences;
    }

    RxSharedPreferences createPreferences() {
        return RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(this));
    }

}
