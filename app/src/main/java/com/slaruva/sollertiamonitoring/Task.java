package com.slaruva.sollertiamonitoring;

import android.content.Context;
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

    //public View toView();

    /**
     * Represents the task (briefly) in View object
     * @param context current context
     * @return View object
     */
    public View toIndexView(Context context);
}
