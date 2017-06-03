package org.petabytes.awesomeblogs.util;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

public class Intents {

    public static Intent createShareIntent(@NonNull String title, @NonNull String link) {
        return new Intent(android.content.Intent.ACTION_SEND)
            .setType("text/plain")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Intent.EXTRA_SUBJECT, title)
            .putExtra(Intent.EXTRA_TEXT, link);
    }

    public static Intent createStoreIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.petabytes.awesomeblogs"));
    }

    public static Intent createGitHubIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jungilhan/awesome-blogs-android"));
    }
}
