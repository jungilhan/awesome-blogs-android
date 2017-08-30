package org.petabytes.api.source.remote;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

import io.realm.RealmList;

class Feed {

    @SerializedName("title")
    private String title;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("entries")
    private List<Entry> entries;

    public String getTitle() {
        return title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<Entry> getEntries() {
        return entries != null ? entries : Collections.<Entry>emptyList();
    }

    @Override
    public String toString() {
        return "Feed{" +
            "title='" + title + '\'' +
            ", updatedAt='" + updatedAt + '\'' +
            ", entries=" + entries +
            '}';
    }

    org.petabytes.api.source.local.Feed toPersist(@NonNull String category) {
        org.petabytes.api.source.local.Feed feed = new org.petabytes.api.source.local.Feed();
        feed.setCategory(category);
        feed.setTitle(title);
        feed.setUpdatedAt(updatedAt);
        feed.setExpires(System.currentTimeMillis() + (30 * DateUtils.MINUTE_IN_MILLIS));
        RealmList<org.petabytes.api.source.local.Entry> entries = new RealmList<>();
        for (Entry e : this.entries) {
            if (!e.isHidden()) {
                entries.add(e.toPersist());
            }
        }
        feed.setEntries(entries);
        return feed;
    }
}
