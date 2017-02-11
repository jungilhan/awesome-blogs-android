package com.petabytes.awesomeblogs;

import org.petabytes.api.Api;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;

import timber.log.Timber;

public class DebugAwesomeBlogsApp extends AwesomeBlogsApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected Api createApi() {
        return new Api(true);
    }
}
