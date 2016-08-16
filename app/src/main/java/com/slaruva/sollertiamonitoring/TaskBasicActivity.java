package com.slaruva.sollertiamonitoring;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.orm.SugarRecord;
import com.orm.util.NamingHelper;
import com.slaruva.sollertiamonitoring.portcheck.PortCheckOptionsActivity;

import java.util.List;

public abstract class TaskBasicActivity<T extends SugarRecord & Task, L extends SimpleLog>
        extends AppCompatActivity implements AbsListView.OnScrollListener {

    public static final String TASK_ID_TAG = "TASK_ID_TAG";
    public static int PAGE_SIZE = 64;

    protected Toolbar toolbar;
    protected SharedMenuFragment sharedMenu;
    protected ArrayAdapter<L> adapter;
    protected long tId;
    protected Task task;

    protected abstract Class _getLogClass();
    protected abstract Class _getTaskClass();
    protected abstract ArrayAdapter<L> createAdapter(Context context, int layoutResourceId,
                                                     List<L> logs);
    protected abstract int getLogsLayoutID();
    public abstract void onSave(MenuItem item);


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

        List<L> logs = getFirstPageLogsNewestFirst();
        ListView logList = (ListView)findViewById(R.id.log_list);
        adapter = createAdapter(this, getLogsLayoutID(), logs);
        logList.setAdapter(adapter);
        logList.setOnScrollListener(this);

        if (logs.size() == 0) {
            TextView no_logs = (TextView) findViewById(R.id.no_logs_message);
            no_logs.setVisibility(View.VISIBLE);
        }

        initToolbar(task.getIp(), savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(getOnRefreshListener());
    }

    class BasicOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            clearAndUpdateAdapter();
        }
    }
    protected SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return new BasicOnRefreshListener();
    }

    @SuppressWarnings("unchecked")
    protected List<L> getFirstPageLogsNewestFirst() {
        return getLogs(0, PAGE_SIZE);
    }
    /**
     * Returns logs followed by the last shown in adapter, sorted by date
     */
    @SuppressWarnings("unchecked")
    protected List<L> getLogsNewestFirst() {
        return getLogs(adapter.getCount(), PAGE_SIZE);
    }

    @SuppressWarnings("unchecked")
    protected List<L> getLogs(long from, long how_many) {
        return SugarRecord.findWithQuery(_getLogClass(),
                "SELECT * FROM " + NamingHelper.toSQLName(_getLogClass()) +
                        " WHERE task_parent = ? " + "ORDER BY id DESC LIMIT ? " +
                        "OFFSET ?",
                Long.toString(tId), Long.toString(how_many),
                Long.toString(from));
    }

    public void onDeleteLogs() {
        SimpleLog.deleteAll(_getLogClass(), "task_parent = ?", Long.toString(tId));
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    protected void clearAndUpdateAdapter() {;
        AsyncTask<Integer, Integer, List<L>> async = new AsyncTask<Integer, Integer, List<L>>() {
            @Override
            protected void onPreExecute() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            @Override
            protected List<L> doInBackground(Integer... params) {
                return getLogs(0, adapter.getCount());
            }
            @Override
            protected void onPostExecute(List<L> updatedLogs) {
                adapter.clear();
                adapter.addAll(updatedLogs);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };

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
            List<L> newLogs = getLogsNewestFirst();
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
    protected void onResume() {
        super.onResume();
        clearAndUpdateAdapter();
    }
}
