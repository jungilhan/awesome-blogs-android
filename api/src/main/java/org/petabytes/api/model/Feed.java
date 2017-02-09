package org.petabytes.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class Feed extends Model {

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
}
