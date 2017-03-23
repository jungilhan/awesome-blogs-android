package org.petabytes.awesomeblogs.digest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.annimon.stream.Optional;
import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Preferences;

import java.lang.annotation.Retention;
import java.util.Calendar;
import java.util.Random;

import hugo.weaving.DebugLog;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class Schedulers {

    @Retention(SOURCE)
    @IntDef({MORNING, EVENING})
    @interface Type {}

    public static final int MORNING = 0;
    public static final int EVENING = 1;

    public static void set(@NonNull Context context) {
        set(context, getDigestAt(MORNING), MORNING);
        set(context, getDigestAt(EVENING), EVENING);
    }

    @DebugLog
    public static void set(@NonNull Context context, long digestAtMillis, @Type int type) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, digestAtMillis, AlarmManager.INTERVAL_DAY,
            PendingIntent.getService(context, type, DigestService.intent(context, type), PendingIntent.FLAG_UPDATE_CURRENT));
        Analytics.event(Analytics.Event.SCHEDULE_DIGEST, Analytics.Param.TYPE, type == MORNING ? "morning" : "evening");
    }

    private static long getDigestAt(@Type int type) {
        Calendar calendar = Calendar.getInstance();
        Preference<Long> digestPreference = type == MORNING ? Preferences.morningDigestAt() : Preferences.eveningDigestAt();
        long digest = Optional.ofNullable(digestPreference.get()).orElse(0L);
        if (digest > System.currentTimeMillis()) {
            calendar.setTimeInMillis(digest);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, type == MORNING ? 9 : 19);
            calendar.set(Calendar.MINUTE, new Random().nextInt(30));
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.DATE, calendar.before(Calendar.getInstance()) ? 1 : 0);
            digestPreference.set(calendar.getTimeInMillis());
        }
        return calendar.getTimeInMillis();
    }
}
