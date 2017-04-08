package org.petabytes.awesomeblogs.author;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.awesomeblogs.util.Strings;
import org.petabytes.coordinator.ActivityGraph;

public class AuthorActivity extends AwesomeActivity {

    public static final String AUTHOR = "author";

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.author)
            .coordinator(R.id.bottom_sheet, new AuthorCoordinator(this, getStringExtra(AUTHOR), this::finish))
            .build();
    }

    public String getStringExtra(@NonNull String name) {
        String extra = getIntent().getStringExtra(name);
        return extra != null ? extra : Strings.EMPTY;
    }

    public static Intent intent(@NonNull Context context, @NonNull String author) {
        Intent intent = new Intent(context, AuthorActivity.class);
        intent.putExtra(AUTHOR, author);
        return intent;
    }
}
