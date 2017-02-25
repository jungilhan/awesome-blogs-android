package org.petabytes.awesomeblogs.util;

import android.os.Bundle;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;

import java.util.Map;

import hugo.weaving.DebugLog;

public final class Analytics {

    private Analytics() {
    }

    public static class Event {

        public static final String MORE_MENU = "more_menu";
        public static final String SHARE = "share";
        public static final String OPEN_IN_BROWSER = "open_in_browser";
        public static final String OPEN_DRAWER = "open_drawer";
        public static final String VIEW_ALL = "view_all";
        public static final String VIEW_DEVELOPER = "view_developer";
        public static final String VIEW_TECH_COMPANY = "view_tech_company";
        public static final String VIEW_INSIGHTFUL = "view_insightful";
        public static final String VIEW_SUMMARY = "view_summary";
        public static final String REFRESH = "refresh";
    }

    public static class Param {

        public static final String TITLE = "title";
        public static final String LINK = "link";
    }

    @DebugLog
    public static void event(@NonNull String name, @NonNull Map<String, String> params) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        AwesomeBlogsApp.get().analytics().logEvent(name, bundle);
    }
}
