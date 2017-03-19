package org.petabytes.awesomeblogs.digest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.annimon.stream.Optional;
import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Preferences;

import java.util.Calendar;
import java.util.Random;

import hugo.weaving.DebugLog;

public class StartUpReceiver extends BroadcastReceiver {

    @DebugLog
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        scheduleAlarm(context);
    }

    public static void scheduleAlarm(@NonNull Context context) {
        Calendar calendar = Calendar.getInstance();
        Preference<Long> digestPreference = Preferences.digestAt();
        long digest = Optional.ofNullable(digestPreference.get()).orElse(0L);
        if (digest > System.currentTimeMillis()) {
            calendar.setTimeInMillis(digest);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, new Random().nextInt(30));
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.DATE, calendar.before(Calendar.getInstance()) ? 1 : 0);
            digestPreference.set(calendar.getTimeInMillis());
        }
        scheduleAlarm(context, calendar.getTimeInMillis());
    }

    @DebugLog
    public static void scheduleAlarm(@NonNull Context context, long digestAtMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, digestAtMillis, AlarmManager.INTERVAL_DAY,
            PendingIntent.getService(context, 0, new Intent(context, DigestService.class), PendingIntent.FLAG_UPDATE_CURRENT));
        Analytics.event(Analytics.Event.SCHEDULE_DIGEST);
    }
}
