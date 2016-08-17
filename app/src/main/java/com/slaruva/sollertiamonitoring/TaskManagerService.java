package com.slaruva.sollertiamonitoring;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.util.Log;

import com.slaruva.sollertiamonitoring.integrity.Integrity;
import com.slaruva.sollertiamonitoring.ping.Ping;
import com.slaruva.sollertiamonitoring.portcheck.PortCheck;

import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

/**
 * The service that responses for execution all tasks in background
 */
public class TaskManagerService extends IntentService {
    public static final String TAG = "TaskManagerService";
    public static final int ALARM_ID = 33333;

    public TaskManagerService() {
        super("TaskManagerService");
    }

    public static long getInterval(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        int key = sharedPreferences.getInt("pref_portcheck_tries", 2);
        switch(key) {
            case 0:
                return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            case 1:
                return AlarmManager.INTERVAL_HALF_HOUR;
            case 2:
                return AlarmManager.INTERVAL_HOUR;
            case 3:
                return AlarmManager.INTERVAL_HALF_DAY;
            default:
                return AlarmManager.INTERVAL_DAY;
        }
    }

    public boolean showNotifications(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean("pref_show_notifications", true);
    }

    /**
     * Sets up AlarmManager
     *
     * @param context current context
     */
    public static void setAlarm(Context context) {
        // TODO for some reason after turning off airplane mode stops working.
        Log.d(TAG, "Setting alarm...");
        Intent intent = new Intent(context, TaskManagerService.class);
        PendingIntent pi = PendingIntent.getService(context, ALARM_ID, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long interval = getInterval(context);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, interval, interval, pi);
    }

    public static void disableAlarm(Context context) {
        Log.d(TAG, "Disabling alarm...");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TaskManagerService.class);
        PendingIntent pi = PendingIntent.getService(context, ALARM_ID, intent, 0);
        am.cancel(pi);
    }

    /**
     * Retrieves all available tasks of all types and adds them into
     * a single vector.
     *
     * @return list of all tasks
     */
    public static List<Task> getAllTasks() {
        List<Task> list = new Vector<>();
        list.addAll(PortCheck.listAll(PortCheck.class));
        list.addAll(Ping.listAll(Ping.class));
        list.addAll(Ping.listAll(Integrity.class));
        return list;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "In service...");
        boolean connect = haveInternetConnection();
        BridgeServiceToApp bridge = new BridgeServiceToApp();
        bridge.setLastSession(connect);
        bridge.setDatetime();
        bridge.save();
        if (!connect) {
            return;
        }

        boolean showNotifications = showNotifications(this);

        Log.d(TAG, "Executing tasks...");
        List<Task> tasks = getAllTasks();
        for (Task t : tasks) {
            if(t.isEnabled() && !t.execute(this) && showNotifications) {
                if(t.countRecentFailedLogs() > t.getWarningLimit())
                    new NotificationController(this)
                            .notifyAboutFailedExecution();
            }
        }
    }

    public boolean haveInternetConnection() {
        String[] stableAddresses = {"google.com", "yahoo.com", "github.com"};
        try {
            for (String ip : stableAddresses) {
                InetAddress iAddr = InetAddress.getByName(ip);
                if (iAddr.toString().equals("")) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static final String SERVICE_STATE = "SERVICE_STATE";
    public static boolean isServiceOn(Context cont) {
        return cont
                .getSharedPreferences(cont.getString(R.string.preference_main), MODE_PRIVATE)
                .getBoolean(SERVICE_STATE, Boolean.TRUE);
    }

    public static void setService(boolean val, Context cont) {
        SharedPreferences sharedPref = cont.getSharedPreferences(
                cont.getString(R.string.preference_main), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SERVICE_STATE, val);
        editor.apply();
    }
}
