package org.petabytes.api.source.remote;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

class Entry {

    @SerializedName("author")
    private String author;

    @SerializedName("title")
    private String title;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("summary")
    private String summary;

    @SerializedName("link")
    private String link;

    private Entry(@NonNull String author, @NonNull String title,
                  @NonNull String updatedAt, @NonNull String summary, @NonNull String link) {
        this.author = author;
        this.title = title;
        this.updatedAt = updatedAt;
        this.summary = summary;
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getSummary() {
        return summary;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "Entry{" +
            "author='" + author + '\'' +
            ", title='" + title + '\'' +
            ", updatedAt='" + updatedAt + '\'' +
            ", summary='" + summary + '\'' +
            ", link='" + link + '\'' +
            '}';
    }

    org.petabytes.api.source.local.Entry toPersist() {
        org.petabytes.api.source.local.Entry entry = new org.petabytes.api.source.local.Entry();
        entry.setAuthor(author);
        entry.setTitle(title);
        entry.setUpdatedAt(updatedAt);
        entry.setSummary(summary);
        entry.setLink(link);
        entry.setCreatedAt(System.currentTimeMillis());
        return entry;
    }
}
