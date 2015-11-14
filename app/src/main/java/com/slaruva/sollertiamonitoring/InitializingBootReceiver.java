package com.slaruva.sollertiamonitoring;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class that sets up AlarmManager on device boot
 */
public class InitializingBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            TaskManagerService.setAlarm(context);
        }
    }
}
