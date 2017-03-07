package org.petabytes.api.source.local;

import android.support.annotation.NonNull;

import org.petabytes.api.util.Dates;

import java.text.ParsePosition;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

class Migration implements RealmMigration {

    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        switch ((int) oldVersion) {
            case 0:
                schema.get("Feed")
                    .addField("expires", long.class);
            case 1:
                schema.get("Entry")
                    .addField("createdAt", long.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            String date = obj.getString("updatedAt");
                            long time = Dates.getDefaultDateFormats().parse(date, new ParsePosition(0)).getTime();
                            obj.setLong("createdAt", time);
                        }
                    });
        }
    }
}
