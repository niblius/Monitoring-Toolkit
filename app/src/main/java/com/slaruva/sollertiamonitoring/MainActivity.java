package com.slaruva.sollertiamonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

    @Override
    protected void onResume() {
        super.onResume();

        List<Task> tasks;
        TasksAdapter adapter;
        ListView taskList = (ListView)findViewById(R.id.task_list);
        tasks = TaskManagerService.getAllTasks();
        adapter = new TasksAdapter(tasks);
        taskList.setAdapter(adapter);
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = ((Task) parent.getItemAtPosition(position))
                        .getIntentToDetailedInfo(MainActivity.this);
                startActivity(i);
            }
        });
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
