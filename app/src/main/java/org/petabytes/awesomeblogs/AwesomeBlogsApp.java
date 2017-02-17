package org.petabytes.awesomeblogs;

import android.app.Application;
import android.preference.PreferenceManager;

import com.f2prateek.rx.preferences.RxSharedPreferences;

import org.petabytes.api.Api;

public class AwesomeBlogsApp extends Application {

    private static AwesomeBlogsApp instance;
    private Api api;
    private RxSharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
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
