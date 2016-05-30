package com.slaruva.sollertiamonitoring;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Class that sets up AlarmManager on device boot
 */
public class InitializingBootReceiver extends BroadcastReceiver {
    private static final String TAG = "InitializingBootReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "On boot received...");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
                && TaskManagerService.isServiceOn(context)) {
            TaskManagerService.setAlarm(context);
        }
    }
}
