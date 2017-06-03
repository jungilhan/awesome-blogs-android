package org.petabytes.api.source.local;

import android.support.annotation.NonNull;

import org.petabytes.api.util.Dates;

import java.text.ParsePosition;
import java.util.Set;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

import static okhttp3.Protocol.get;

class Migration implements RealmMigration {

    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        switch ((int) oldVersion) {
            case 0:
                schema.get("Feed")
                    .addField("expires", long.class);
            case 1:
                Set<String> fields = schema.get("Entry").getFieldNames();
                if (!fields.contains("createdAt")) {
                    schema.get("Entry")
                        .addField("createdAt", long.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                String date = obj.getString("updatedAt");
                                long time = Dates.getDefaultDateFormats().parseDateTime(date).getMillis();
                                obj.setLong("createdAt", time);
                            }
                        });
                }
            case 2:
                schema.create("Favorite")
                    .addField("title", String.class)
                    .addField("author", String.class)
                    .addField("updatedAt", String.class)
                    .addField("summary", String.class)
                    .addField("link", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("favoriteAt", long.class);
        }
    }
}
