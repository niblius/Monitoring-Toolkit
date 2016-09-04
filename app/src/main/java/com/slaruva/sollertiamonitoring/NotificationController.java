package com.slaruva.sollertiamonitoring;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;


public class NotificationController {
    private Context context;

    public NotificationController(Context c) {
        context = c;
    }

    public boolean playSound() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("pref_play_notification_sound", true);
    }

    private static final int FAILED_EXECUTION_NOTIFICATION_ID = 1;
    public void notifyAboutFailedExecution() {
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.failed_notification)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.failed_execution_notification_text))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(resultPendingIntent)
                        .setOnlyAlertOnce(true);
        if(playSound()) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            // TODO test vibration
            mBuilder.setVibrate(new long[] {300, 300, 300});
        }

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(FAILED_EXECUTION_NOTIFICATION_ID, mBuilder.build());
    }
}
