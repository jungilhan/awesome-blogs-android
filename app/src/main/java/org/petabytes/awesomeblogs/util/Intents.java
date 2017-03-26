package org.petabytes.awesomeblogs.util;

import android.content.Intent;
import android.support.annotation.NonNull;

public class Intents {

    public static Intent createShareIntent(@NonNull String title, @NonNull String link) {
        return new Intent(android.content.Intent.ACTION_SEND)
            .setType("text/plain")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Intent.EXTRA_SUBJECT, title)
            .putExtra(Intent.EXTRA_TEXT, link);
    }
}
