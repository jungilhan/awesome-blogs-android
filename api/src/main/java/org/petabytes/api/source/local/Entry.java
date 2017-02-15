package org.petabytes.api.source.local;

import org.petabytes.api.util.Dates;

import java.text.ParsePosition;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Entry extends RealmObject {

    private String author;
    private String title;
    private String updatedAt;
    private String summary;
    private @PrimaryKey String link;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public static String getFormattedAuthorUpdatedAt(Entry entry) {
        return "by " + entry.getAuthor() + "  ·  " + Dates.getRelativeTimeString(
            Dates.getDefaultDateFormats().parse(entry.getUpdatedAt(), new ParsePosition(0)).getTime());
    }
}
