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

    @SerializedName("link")
    private String link;

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
            "link='" + link + '\'' +
            ", summary='" + summary + '\'' +
            ", updatedAt='" + updatedAt + '\'' +
            ", title='" + title + '\'' +
            ", author='" + author + '\'' +
            '}';
    }
}
