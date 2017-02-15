package org.petabytes.api.source.local;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Feed extends RealmObject {

    @PrimaryKey
    private String category;
    private String title;
    private String updatedAt;
    private RealmList<Entry> entries;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public RealmList<Entry> getEntries() {
        return entries != null ? entries : new RealmList<Entry>();
    }

    public void setEntries(RealmList<Entry> entries) {
        this.entries = entries;
    }
}
