package org.petabytes.api.source.local;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.petabytes.api.util.Dates;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Entry extends RealmObject {

    private String title;
    private String author;
    private String updatedAt;
    private String summary;
    private long createdAt;
    private @PrimaryKey String link;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Entry)) {
            return false;
        }
        return TextUtils.equals(link, ((Entry) other).getLink());
    }

    public static String getFormattedAuthorUpdatedAt(@NonNull Entry entry) {
        return "by " + entry.getAuthor() + "  ·  " + Dates.getRelativeTimeString(
            Dates.getDefaultDateFormats().parseDateTime(entry.getUpdatedAt()).getMillis());
    }

    public static String getFormattedAuthorUpdatedAt(@NonNull String author, @NonNull String updatedAt) {
        return "by " + author + "  ·  " + Dates.getRelativeTimeString(
            Dates.getDefaultDateFormats().parseDateTime(updatedAt).getMillis());
    }
}
