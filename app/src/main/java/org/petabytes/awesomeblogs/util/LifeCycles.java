package org.petabytes.awesomeblogs.util;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class LifeCycles {

    public static class Activity implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull android.app.Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull android.app.Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull android.app.Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull android.app.Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull android.app.Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull android.app.Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull android.app.Activity activity) {

        }
    }
}
