package com.slaruva.sollertiamonitoring;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.orm.util.NamingHelper;

import java.util.List;

public abstract class TaskBasicActivity<T extends SugarRecord & Task, L extends SimpleLog>
        extends AppCompatActivity implements AbsListView.OnScrollListener {

    private static final String TAG = "TaskBasicActivity";
    public static final String TASK_ID_TAG = "TASK_ID_TAG";
    public static int PAGE_SIZE = 64;

    protected Toolbar toolbar;
    protected SharedMenuFragment sharedMenu;
    protected ArrayAdapter<L> adapter;
    protected long tId;
    protected Task task;
    protected CheckBox showFailsOnlyCheck;

    protected abstract Class _getLogClass();
    protected abstract Class _getTaskClass();
    protected abstract ArrayAdapter<L> createAdapter(Context context, int layoutResourceId,
                                                     List<L> logs);
    protected abstract int getLogsLayoutID();
    public abstract void onSave(MenuItem item);

    private static final String SHOW_FAILS_PREF = "SHOW_FAILS_PREF";
    protected void setShowFailsPref(boolean show) {
        Log.i(TAG, "SHOW_FAILS_PREF is being set to " + show);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SHOW_FAILS_PREF, show);
        editor.apply();
    }

    protected boolean getShowFailsPref() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SHOW_FAILS_PREF, false);
    }

    protected void errorVisibilityOn() {
        TextView err = (TextView) findViewById(R.id.error_view);
        err.setVisibility(View.VISIBLE);
    }

    protected void errorVisibilityOff() {
        TextView err = (TextView) findViewById(R.id.error_view);
        err.setVisibility(View.INVISIBLE);
    }

    protected void setIpToField() {
        EditText ip = (EditText)findViewById(R.id.ip);
        ip.setText(task.getIp());
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    @SuppressWarnings("unchecked")
    protected void init(Bundle savedInstanceState) {
        tId = getIntent().getExtras().getLong(TASK_ID_TAG);
        task = (T)T.findById(_getTaskClass(), tId);

        boolean show_failed_only = getShowFailsPref();
        List<L> logs = getFirstPageLogsNewestFirst(show_failed_only);
        ListView logList = (ListView)findViewById(R.id.log_list);
        adapter = createAdapter(this, getLogsLayoutID(), logs);
        logList.setAdapter(adapter);
        logList.setOnScrollListener(this);

        if (logs.size() == 0 && !show_failed_only) {
            TextView no_logs = (TextView) findViewById(R.id.no_logs_message);
            no_logs.setVisibility(View.VISIBLE);
        }

        initToolbar(task.getIp(), savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(getOnRefreshListener());

        showFailsOnlyCheck = (CheckBox) findViewById(R.id.show_only_fails);
        showFailsOnlyCheck.setChecked(show_failed_only);
        showFailsOnlyCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setShowFailsPref(isChecked);
                clearAndUpdateAdapter(isChecked);
            }
        });
    }

    class BasicOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            clearAndUpdateAdapter(getShowFailsPref());
        }
    }
    protected SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return new BasicOnRefreshListener();
    }

    @SuppressWarnings("unchecked")
    protected List<L> getFirstPageLogsNewestFirst(boolean failed_only) {
        return getLogs(0, PAGE_SIZE, failed_only);
    }
    /**
     * Returns logs followed by the last shown in adapter, sorted by date
     */
    @SuppressWarnings("unchecked")
    protected List<L> getLogsNewestFirst(boolean failed_only) {
        return getLogs(adapter.getCount(), PAGE_SIZE, failed_only);
    }

    @SuppressWarnings("unchecked")
    protected List<L> getLogs(long from, long how_many, boolean failed_only) {
        if(failed_only)
            return SugarRecord.findWithQuery(_getLogClass(),
                    "SELECT * FROM " + NamingHelper.toSQLName(_getLogClass()) +
                            " WHERE task_parent = ? AND state = ? " + "ORDER BY id DESC LIMIT ? " +
                            "OFFSET ?",
                    Long.toString(tId),
                    Integer.toString(SimpleLog.State.toInteger(SimpleLog.State.FAIL)),
                    Long.toString(how_many),
                    Long.toString(from));
        else
            return SugarRecord.findWithQuery(_getLogClass(),
                "SELECT * FROM " + NamingHelper.toSQLName(_getLogClass()) +
                        " WHERE task_parent = ? " + "ORDER BY id DESC LIMIT ? " +
                        "OFFSET ?",
                Long.toString(tId),
                Long.toString(how_many),
                Long.toString(from));
    }

    public void onDeleteLogs() {
        SimpleLog.deleteAll(_getLogClass(), "task_parent = ?", Long.toString(tId));
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    private class AsyncLogsUpdater extends AsyncTask<Integer, Integer, List<L>> {
        private boolean show_fails;
        public AsyncLogsUpdater(boolean show_fails) {
            this.show_fails = show_fails;
        }

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<L> doInBackground(Integer... params) {
            long count = (adapter.getCount() == 0) ? PAGE_SIZE : adapter.getCount();
            return getLogs(0, count, show_fails);
        }

        @Override
        protected void onPostExecute(List<L> updatedLogs) {
            adapter.clear();
            adapter.addAll(updatedLogs);
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    protected void clearAndUpdateAdapter(boolean show_fails) {;
        AsyncLogsUpdater async = new AsyncLogsUpdater(show_fails);
        async.execute();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) { }

    private int lastItemNumb = 0;
    private int preLastItemNumb = 0;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        lastItemNumb = firstVisibleItem + visibleItemCount;
        if(preLastItemNumb != lastItemNumb && lastItemNumb == totalItemCount) {
            preLastItemNumb = lastItemNumb;
            List<L> newLogs = getLogsNewestFirst(getShowFailsPref());
            adapter.addAll(newLogs);
            adapter.notifyDataSetChanged();
        }
    }

    protected void initToolbar(String title, Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        ActionBar mActionBar;
        if ((mActionBar = getSupportActionBar()) != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(savedInstanceState == null) {
            sharedMenu = new SharedMenuFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(sharedMenu, SharedMenuFragment.TAG);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_activity_menu, menu);

        MenuItem mi = menu.findItem(R.id.on_off_switch_item);
        View v = mi.getActionView();
        Switch on_off = (Switch) v.findViewById(R.id.switchForToolbar);
        on_off.setChecked(task.isEnabled());
        on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = TaskBasicActivity.this;
                task.setEnabled(isChecked);
                Toast t = Toast.makeText(getApplicationContext(),
                        getString((isChecked) ? R.string.task_is_on : R.string.task_is_off),
                        Toast.LENGTH_SHORT);
                t.show();

                onSave(null);
            }
        });

        return true;
    }

    public void onClearLogs(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SureDeleteLogsDialogListener listener = new SureDeleteLogsDialogListener();
        builder.setMessage(R.string.are_you_sure_clear_logs).setPositiveButton(R.string.yes, listener)
                .setNegativeButton(R.string.no, listener).show();
    }

    private class SureDeleteLogsDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE)
                onDeleteLogs();
        }
    }

    public void onDelete(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SureDeleteTaskDialogListener listener = new SureDeleteTaskDialogListener();
        builder.setMessage(R.string.are_you_sure_delete_task).setPositiveButton(R.string.yes, listener)
                .setNegativeButton(R.string.no, listener).show();
    }

    private class SureDeleteTaskDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE)
                onDeleteTask();
        }
    }

    protected void onDeleteTask() {
        L.deleteAll(_getLogClass(), "task_parent = ?", "" + tId);
        SugarRecord.delete(task);
        this.finish();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onResume() {
        super.onResume();
        // updating task, since we could come from task options page.
        task = (T)T.findById(_getTaskClass(), tId);
        clearAndUpdateAdapter(getShowFailsPref());
    }

    @Override
    protected void onPause() {
        super.onPause();
        onSave(null);
    }
}
