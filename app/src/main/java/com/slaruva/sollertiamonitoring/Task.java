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
    public void execute(Context context);

    /**
     * Represents the task (briefly) in View object
     * @param context current context
     * @return View object
     */
    public View getRowView(Context context, View lastRowView, ViewGroup parent);

    /**
     * Represents the task (completely) in Activity
     * and returns intent to that activity
     * @return
     */
    public Intent getIntentToDetailedInfo(Context context);

    public long countSuccessfulLogs();
    public long countAllLogs();

    public SimpleLog getLastLog();

    public String getIp();
}
