package org.petabytes.api.model;

import com.google.gson.annotations.SerializedName;

public class Entry extends Model {

    @SerializedName("author")
    private String author;

    @SerializedName("title")
    private String title;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("summary")
    private String summary;

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

    @Override
    public String toString() {
        return "Entry{" +
            "author='" + author + '\'' +
            ", title='" + title + '\'' +
            ", updatedAt='" + updatedAt + '\'' +
            ", summary='" + summary + '\'' +
            '}';
    }
}
