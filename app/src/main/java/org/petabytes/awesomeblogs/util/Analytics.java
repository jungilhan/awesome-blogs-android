package org.petabytes.awesomeblogs.util;

import android.os.Bundle;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;

import java.util.Collections;
import java.util.Map;

import hugo.weaving.DebugLog;

public final class Analytics {

    private Analytics() {
    }

    public static class Event {

        public static final String MORE_MENU = "more_menu";
        public static final String COPY_LINK = "copy_link";
        public static final String SHARE = "share";
        public static final String OPEN_IN_BROWSER = "open_in_browser";
        public static final String OPEN_DRAWER = "open_drawer";
        public static final String VIEW_ALL = "view_all";
        public static final String VIEW_DEVELOPER = "view_developer";
        public static final String VIEW_TECH_COMPANY = "view_tech_company";
        public static final String VIEW_INSIGHTFUL = "view_insightful";
        public static final String VIEW_SUMMARY = "view_summary";
        public static final String VIEW_HISTORY = "view_history";
        public static final String VIEW_HISTORY_ITEM = "view_history_item";
        public static final String VIEW_SEARCH = "view_search";
        public static final String VIEW_SEARCH_ITEM = "view_search_item";
        public static final String REFRESH = "refresh";
        public static final String NOTIFY_FRESH_ENTRIES = "notify_fresh_entries";
        public static final String VIEW_FRESH_ENTRIES = "view_fresh_entries";
        public static final String SEND_DIGEST = "send_digest";
        public static final String VIEW_DIGEST = "view_digest";
        public static final String SCHEDULE_DIGEST = "schedule_digest";
        public static final String INSTALL_REFERRER = "install_referrer";
        public static final String VIEW_SIBLING = "view_sibling";
        public static final String VIEW_AUTHOR = "view_author";
        public static final String VIEW_SETTINGS = "view_settings";
        public static final String SETTINGS_DIGEST = "settings_digest";
        public static final String SETTINGS_SILENT = "settings_silent";
    }

    public static class Param {

        public static final String FEEDS = "feeds";
        public static final String TITLE = "title";
        public static final String LINK = "link";
        public static final String AUTHOR = "author";
        public static final String SIBLING = "sibling";
        public static final String HISTORY = "history";
        public static final String SEARCH = "search";
        public static final String TYPE = "type";
        public static final String SIZE = "size";
        public static final String FROM = "from";
        public static final String ENABLED = "enabled";
    }

    public static void event(@NonNull String name) {
        event(name, Collections.emptyMap());
    }

    public static void event(@NonNull String name, @NonNull String key, @NonNull String value) {
        event(name, Collections.singletonMap(key, value));
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
