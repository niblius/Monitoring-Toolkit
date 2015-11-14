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
    private SharedPreferences sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Sollertia", "Initialization...");
        SugarContext.init(this);
        setPreferences();
        doFirstTimeRoutine();
    }

    private void doFirstTimeRoutine() {
        //TODO greetings
        boolean wasStated = sharedPref.getBoolean(
                getString(R.string.was_previously_started), Boolean.FALSE);
        if(!wasStated) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.was_previously_started), Boolean.TRUE);
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