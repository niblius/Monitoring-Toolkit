package com.slaruva.sollertiamonitoring;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.orm.SugarContext;

/**
 * Performs basic initialization of:
 * database
 * preferences
 * greetings dialog
 * sets up alarms first time
 */
public class Sollertia extends Application {
    private static final String TAG = "Sollertia";
    private SharedPreferences sharedPref;

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
        //TODO greetings
        boolean wasStated = sharedPref.getBoolean(
                PREFERENCE_WAS_PREVIOUSLY_STARTED, Boolean.FALSE);
        if(!wasStated) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(PREFERENCE_WAS_PREVIOUSLY_STARTED, Boolean.TRUE);
            editor.commit();
            TaskManagerService.setAlarm(this);
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
        this.sharedPref = getSharedPreferences(
                getString(R.string.preference_main), MODE_PRIVATE);
    }
}