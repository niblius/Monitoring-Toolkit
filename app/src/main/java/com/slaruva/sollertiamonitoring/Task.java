package com.slaruva.sollertiamonitoring;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Interface for each task, all external operations with the task
 * are performed only through this interface
 */
public interface Task {
    /**
     * Performs execution of the task
     * @param context current context
     */
    boolean execute(Context context);

    /**
     * Represents the task (briefly) in View object
     * @param context current context
     * @return View object
     */
    View getRowView(Context context, View lastRowView, ViewGroup parent);

    /**
     * Represents the task (completely) in Activity
     * and returns intent to that activity
     * @return
     */
    Intent getIntentToDetailedInfo(Context context);

    long countSuccessfulLogs();

    long countAllLogs();

    long countAllRecentLogs();

    long countFailedLogs(long datetime);

    long countRecentFailedLogs();

    int getWarningLimit();

    void setWarningLimit(int n);

    SimpleLog getLastLog();

    String getIp();

    boolean isEnabled();

    void setEnabled(boolean b);

    int getNumberOfTries();

    void setNumberOfTries(int n);

    int getPriority();

    void setPriority(int priority);

    String getExportString();
}
