package com.slaruva.sollertiamonitoring;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Main activity of the app, currently responses for displaying
 * all current tasks
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    class MyRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            updateAdapter();
        }
    }
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpListView();
        initToolbar(savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new MyRefreshListener());

        showLastSessionPopUp();
    }

    private TasksAdapter adapter;
    private ListView taskList;
    private void setUpListView() {
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

    private Toolbar toolbar;
    private SharedMenuFragment sharedMenu;
    private void initToolbar(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if(savedInstanceState == null) {
            sharedMenu = new SharedMenuFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(sharedMenu, SharedMenuFragment.TAG);
            transaction.commit();
        }

        ImageView img = (ImageView) findViewById(R.id.logo);
        img.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        MenuItem mi = menu.findItem(R.id.on_off_switch_item);
        View v = mi.getActionView();
        Switch on_off = (Switch) v.findViewById(R.id.switchForToolbar);
        on_off.setChecked(TaskManagerService.isServiceOn(getApplicationContext()));
        on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getApplicationContext();
                if(isChecked)
                    TaskManagerService.setAlarm(context);
                else
                    TaskManagerService.disableAlarm(context);

                Toast t = Toast.makeText(getApplicationContext(),
                        getString((isChecked) ? R.string.task_execution_on : R.string.task_execution_off),
                        Toast.LENGTH_SHORT);
                t.show();

                TaskManagerService.setService(isChecked, context);
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdapter();
    }

    private void showLastSessionPopUp() {
        List<BridgeServiceToApp> bridges = BridgeServiceToApp.find(BridgeServiceToApp.class,
                "last_session != 0",
                new String[]{},
                null, "id DESC", "1");
        if(bridges.isEmpty())
            return;
        BridgeServiceToApp bridge = bridges.get(0);
        CharSequence text = getString(R.string.last_session_was) +
                bridge.getDatetime(new SimpleDateFormat(" dd/MM/yyyy hh:mm a"));
        Toast t = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        t.show();
    }

    class TasksLoader extends AsyncTask<Integer, Integer, List<Task>> {
        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        @Override
        protected List<Task> doInBackground(Integer... params) {
            return TaskManagerService.getAllTasks();
        }
        @Override
        protected void onPostExecute(List<Task> tasks) {
            adapter.clear();
            adapter.addAll(tasks);
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    private void updateAdapter() {
        TasksLoader tl = new TasksLoader();
        tl.execute();
    }

    private final static String DIALOG_TAG = "CREATE_NEW_TASK_DIALOG";
    public void onCreateNewTask(MenuItem item) {
        DialogFragment df = new CreateTaskDialog();
        df.show(getFragmentManager(), DIALOG_TAG);
    }

    public void onExecuteAllTasks(MenuItem item) {
        Intent intent = new Intent(this, TaskManagerService.class);
        startService(intent);
    }
}
