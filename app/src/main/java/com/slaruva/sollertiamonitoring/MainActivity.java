package com.slaruva.sollertiamonitoring;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.Vector;

/**
 * Main activity of the app, currently responses for displaying
 * all current tasks
 */
public class MainActivity extends AppCompatActivity {
    TasksAdapter adapter;
    ListView taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = (ListView)findViewById(R.id.task_list);
        List<Task> tasks = new Vector<>();
        adapter = new TasksAdapter(this, tasks);
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

    @Override
    protected void onResume() {
        super.onResume();
        //  TODO    ADD ONLY NEW, same with all others adapters
        adapter.clear();
        adapter.addAll(TaskManagerService.getAllTasks());
        adapter.notifyDataSetChanged();
    }

    private final static String DIALOG_TAG = "CREATE_NEW_TASK_DIALOG";

    public void gotoCreateTaskActivity(View view) {
        DialogFragment df = new CreateTaskDialog();
        df.show(getFragmentManager(), DIALOG_TAG);
    }
}
