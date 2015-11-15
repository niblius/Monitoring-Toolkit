package com.slaruva.sollertiamonitoring;

import android.content.Context;
import android.content.Intent;
import android.view.View;

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
    public View getRowView(Context context, View lastRowView);

    /**
     * Represents the task (completely) in Activity
     * and returns intent to that activity
     * @return
     */
    public Intent getIntentToDetailedInfo(Context context);
}
