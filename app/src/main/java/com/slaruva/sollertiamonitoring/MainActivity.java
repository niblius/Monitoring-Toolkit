package com.slaruva.sollertiamonitoring;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.slaruva.sollertiamonitoring.integrity.Integrity;
import com.slaruva.sollertiamonitoring.ping.Ping;
import com.slaruva.sollertiamonitoring.portcheck.PortCheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // showLastSessionPopUp();
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
                Context context = MainActivity.this;
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
            List<Task> tasks = TaskManagerService.getAllTasks();
            Collections.sort(tasks, new Comparator<Task>() {
                @Override
                public int compare(Task lhs, Task rhs) {
                    // sort them by alert level if both have recent fails
                    // sort them by alphabet if alert levels are equal, or both have 0 recent fails
                    // if first one have recent fails and the second one does not, first is greater
                    // if opposite - smaller.

                    if(lhs.countRecentFailedLogs() > 0 && rhs.countRecentFailedLogs() > 0) {
                        int lhsAlertLevel = lhs.getPriority() * (int) lhs.countRecentFailedLogs();
                        int rhsAlertLevel = rhs.getPriority() * (int) rhs.countRecentFailedLogs();
                        int diff = rhsAlertLevel - lhsAlertLevel;
                        if (diff == 0) {
                            return lhs.getIp().compareTo(rhs.getIp());
                        } else
                            return diff;
                    }
                    else if (lhs.countRecentFailedLogs() > 0)
                        return -1;
                    else if (rhs.countRecentFailedLogs() > 0)
                        return 1;
                    else
                        return lhs.getIp().compareTo(rhs.getIp());
                }
            });
            return tasks;
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

    public void onImport(MenuItem item) {
        if (ExternalStorage.haveReadPermission(this)) {
            ExternalStorage.requestReadPermission(this);
            return;
        }

        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this, properties);
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            private final Pattern integrityPattern = Pattern.compile(
                    "integrity\\(\\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\'\\);");
            private final Pattern porcheckPattern = Pattern.compile(
                    "portcheck\\(\\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\'\\);");
            private final Pattern pingPattern = Pattern.compile(
                    "ping\\(\\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\', \\'([^']+)\\'\\);");

            @Override
            public void onSelectedFilePaths(String[] filepaths) {
                String filepath = filepaths[0];
                File f = new File(filepath);
                try {
                    FileReader fr = new FileReader(f);
                    BufferedReader reader = new BufferedReader(fr);
                    String ln = null;
                    Matcher matcher;
                    while ((ln = reader.readLine()) != null) {
                        if (ln.startsWith("integrity")) {
                            matcher = integrityPattern.matcher(ln);
                            if (matcher.matches()) {
                                String ip = matcher.group(1), regexp = matcher.group(2);
                                int warningLimit = Integer.parseInt(matcher.group(3)),
                                        numberOftries = Integer.parseInt(matcher.group(4)),
                                        priority = Integer.parseInt(matcher.group(6));
                                boolean enabled = Boolean.parseBoolean(matcher.group(5));
                                Integrity integ = new Integrity();
                                integ.setIp(ip);
                                integ.setRegexp(regexp);
                                integ.setWarningLimit(warningLimit);
                                integ.setNumberOfTries(numberOftries);
                                integ.setPriority(priority);
                                integ.setEnabled(enabled);
                                integ.save();
                            }
                        } else if (ln.startsWith("portcheck")) {
                            matcher = porcheckPattern.matcher(ln);
                            if (matcher.matches()) {
                                String ip = matcher.group(1);
                                int port = Integer.parseInt(matcher.group(2)),
                                        warningLimit = Integer.parseInt(matcher.group(3)),
                                        numberOftries = Integer.parseInt(matcher.group(4)),
                                        priority = Integer.parseInt(matcher.group(6));
                                boolean enabled = Boolean.parseBoolean(matcher.group(5));
                                PortCheck pc = new PortCheck();
                                pc.setIp(ip);
                                pc.setPort(port);
                                pc.setWarningLimit(warningLimit);
                                pc.setNumberOfTries(numberOftries);
                                pc.setPriority(priority);
                                pc.setEnabled(enabled);
                                pc.save();
                            }
                        } else if (ln.startsWith("ping")) {
                            matcher = pingPattern.matcher(ln);
                            if (matcher.matches()) {
                                String ip = matcher.group(1);
                                int warningLimit = Integer.parseInt(matcher.group(2)),
                                        numberOftries = Integer.parseInt(matcher.group(4)),
                                        priority = Integer.parseInt(matcher.group(5));
                                boolean enabled = Boolean.parseBoolean(matcher.group(3));
                                Ping ping = new Ping();
                                ping.setIp(ip);
                                ping.setWarningLimit(warningLimit);
                                ping.setNumberOfTries(numberOftries);
                                ping.setPriority(priority);
                                ping.setEnabled(enabled);
                                ping.save();
                            }
                        }
                    }

                    reader.close();

                    Toast.makeText(getApplicationContext(),
                            getString(R.string.successfully_imported),
                            Toast.LENGTH_SHORT).show();
                    updateAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
    }

    public void onExport(MenuItem item) {
        if (ExternalStorage.haveWritePermission(this)) {
            ExternalStorage.requestWritePermission(this);
            return;
        }

        if (!ExternalStorage.isExternalStorageWritable()) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.not_able_to_save),
                    Toast.LENGTH_LONG).show();
            return;
        }
        String filename = "Sollertia.export";
        File file = new File(ExternalStorage.getAppStorageDir(), filename);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            List<Task> tasks = TaskManagerService.getAllTasks();

            for (Task t : tasks)
                outputStream.write(t.getExportString().getBytes());

            outputStream.close();
            Toast t = Toast.makeText(getApplicationContext(),
                    getString(R.string.response_saved) + " " + file.getPath(),
                    Toast.LENGTH_LONG);
            t.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ExternalStorage.WRITE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onExport(null);
                } else {
                    // do nothing
                }
                return;
            }
            case ExternalStorage.READ_PEMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onImport(null);
                } else {
                    // do nothing
                }
                return;
            }
        }
    }
}
