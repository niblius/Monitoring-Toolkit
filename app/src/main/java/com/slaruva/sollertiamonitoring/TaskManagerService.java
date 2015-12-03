package com.slaruva.sollertiamonitoring;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.slaruva.sollertiamonitoring.ping.Ping;
import com.slaruva.sollertiamonitoring.portcheck.PortCheck;

import java.util.List;
import java.util.Vector;

/**
 * The service that responses for execution all tasks in background
 */
public class TaskManagerService extends IntentService {
    public static final String TAG = "TaskManagerService";

    public static final int ALARM_ID = 33333;

    /**
     * Sets up AlarmManager
     * @param context current context
     */
    public static void setAlarm(Context context) {
        Log.d(TAG, "Setting alarm...");
        Intent intent = new Intent(context, TaskManagerService.class);
        PendingIntent pi = PendingIntent.getService(context, ALARM_ID, intent, 0);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,   //there is no need to wake up device
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
        /*am.setRepeating(AlarmManager.ELAPSED_REALTIME,
                10000, 20000, pi);*/
    }

    public TaskManagerService() { super("TaskManagerService"); }

    /**
     * Retrieves all available tasks of all types and adds them into
     * a single vector.
     * @return list of all tasks
     */
    public static List<Task> getAllTasks() {
        List<Task> list = new Vector<>();
        list.addAll(PortCheck.listAll(PortCheck.class));
        list.addAll(Ping.listAll(Ping.class));
        return list;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Executing tasks...");
        List<Task> tasks = getAllTasks();
        for(Task t : tasks) {
            t.execute(this);
        }
    }
}
