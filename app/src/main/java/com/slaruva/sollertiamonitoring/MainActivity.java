package com.slaruva.sollertiamonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Main activity of the app, currently responses for displaying
 * all current tasks
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Currently works as Index page
     */
    @Override
    protected void onResume() {
        super.onResume();

        ViewGroup taskList = (ViewGroup)findViewById(R.id.task_list);
        taskList.removeAllViews();
        List<Task> tasks = TaskManagerService.getAllTasks();
        for(Task t : tasks) {
            taskList.addView(t.toIndexView(this));
        }
    }

    /**
     * OnClick method for button "Create new task", moves us to CreatePortCheckActivity
     * in future will display dialog with dropdown list of available task types.
     * @param view
     */
    public void gotoCreateTaskActivity(View view) {
        Intent i = new Intent(this, CreatePortCheckActivity.class);
        startActivity(i);
    }

}
