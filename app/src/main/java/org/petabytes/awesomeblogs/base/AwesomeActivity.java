package org.petabytes.awesomeblogs.base;

import android.content.Context;
import android.support.annotation.NonNull;

import org.petabytes.coordinator.Activity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class AwesomeActivity extends Activity {

    @Override
    protected void attachBaseContext(@NonNull Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }
}
