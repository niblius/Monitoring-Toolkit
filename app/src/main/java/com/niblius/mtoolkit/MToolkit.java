package com.niblius.mtoolkit;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.orm.SugarContext;
import com.niblius.mtoolkit.integrity.Integrity;
import com.niblius.mtoolkit.ping.Ping;
import com.niblius.mtoolkit.portcheck.PortCheck;

/**
 * Performs basic initialization of:
 * database
 * preferences
 * greetings dialog
 * sets up alarms first time
 */
public class MToolkit extends Application {
    private static final String TAG = "MToolkit";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Initialization...");
        SugarContext.init(this);
        setPreferences();
        doFirstTimeRoutine();
    }

    private static final String PREFERENCE_WAS_PREVIOUSLY_STARTED =
            "PREFERENCE_WAS_PREVIOUSLY_STARTED";
    private void doFirstTimeRoutine() {
        boolean wasStated = sharedPref.getBoolean(
                PREFERENCE_WAS_PREVIOUSLY_STARTED, Boolean.FALSE);
        if(!wasStated) {
            editor.putBoolean(PREFERENCE_WAS_PREVIOUSLY_STARTED, Boolean.TRUE);
            editor.apply();
            TaskManagerService.setAlarm(this);
            Ping pingExample = new Ping("google.com");
            pingExample.save();
            PortCheck pcExample = new PortCheck("github.com", 80);
            pcExample.save();
            Integrity iExample = new Integrity("http://google.com", ".*[a-fA-F0-9]{2}.*");
            iExample.save();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    private void setPreferences() {
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
    }
}