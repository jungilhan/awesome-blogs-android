package com.petabytes.awesomeblogs;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import org.petabytes.api.Api;
import org.petabytes.awesomeblogs.AwesomeBlogsApp;
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
        return new Api(this, true);
    }
}
