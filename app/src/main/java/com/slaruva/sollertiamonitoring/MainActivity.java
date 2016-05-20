package com.slaruva.sollertiamonitoring;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Main activity of the app, currently responses for displaying
 * all current tasks
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

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

        initToolbar();
    }

    Toolbar toolbar;
    SharedMenuFragment sharedMenu;

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        sharedMenu = new SharedMenuFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(sharedMenu, SharedMenuFragment.TAG);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
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

    public void onCreateNewTask(MenuItem item) {
        DialogFragment df = new CreateTaskDialog();
        df.show(getFragmentManager(), DIALOG_TAG);
    }
}
