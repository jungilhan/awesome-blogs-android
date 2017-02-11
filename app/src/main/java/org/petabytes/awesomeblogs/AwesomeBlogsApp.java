package org.petabytes.awesomeblogs;

import android.app.Application;

import org.petabytes.api.Api;

public class AwesomeBlogsApp extends Application {

    private static AwesomeBlogsApp instance;
    private static Api api;

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
        return new Api(false);
    }
}
