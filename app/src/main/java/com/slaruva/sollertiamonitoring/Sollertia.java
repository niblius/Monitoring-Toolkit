package com.slaruva.sollertiamonitoring;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.orm.SugarContext;
import com.orm.SugarDb;
import com.orm.util.SugarConfig;
import com.slaruva.sollertiamonitoring.ping.Ping;
import com.slaruva.sollertiamonitoring.ping.PingLog;
import com.slaruva.sollertiamonitoring.portcheck.PortCheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.List;

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
        boolean wasStated = sharedPref.getBoolean(
                PREFERENCE_WAS_PREVIOUSLY_STARTED, Boolean.FALSE);
        if(!wasStated) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(PREFERENCE_WAS_PREVIOUSLY_STARTED, Boolean.TRUE);
            editor.apply();
            TaskManagerService.setAlarm(this);
            Ping pingExample = new Ping("google.com");
            pingExample.save();
            PortCheck pcExample = new PortCheck("github.com", 80);
            pcExample.save();
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