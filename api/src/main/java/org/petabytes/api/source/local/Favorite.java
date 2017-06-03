package org.petabytes.api.source.local;

import android.support.annotation.NonNull;

import org.petabytes.api.util.Dates;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Favorite extends RealmObject {

    private String title;
    private String author;
    private String updatedAt;
    private String summary;
    private @PrimaryKey String link;
    private long favoriteAt;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getFavoriteAt() {
        return favoriteAt;
    }

    public void setFavoriteAt(long favoriteAt) {
        this.favoriteAt = favoriteAt;
    }

    public static String getFormattedAuthorUpdatedAt(@NonNull Favorite entry) {
        return "by " + entry.getAuthor() + "  ·  " + Dates.getRelativeTimeString(
            Dates.getDefaultDateFormats().parseDateTime(entry.getUpdatedAt()).getMillis());
    }
}
