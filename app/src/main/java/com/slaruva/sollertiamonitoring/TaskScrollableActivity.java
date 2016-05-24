package com.slaruva.sollertiamonitoring;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.orm.SugarRecord;
import com.orm.util.NamingHelper;

import java.util.List;

public abstract class TaskScrollableActivity<T extends SugarRecord & Task, L extends SimpleLog>
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

    protected void setIpToField() {
        EditText ip = (EditText)findViewById(R.id.ip);
        ip.setText(task.getIp());
    }

    @SuppressWarnings("unchecked")
    protected void init() {
        tId = getIntent().getExtras().getLong(TASK_ID_TAG);
        task = (T)T.findById(_getTaskClass(), tId);

        List<L> logs = getFirstPageLogsNewestFirst();
        ListView logList = (ListView)findViewById(R.id.log_list);
        adapter = createAdapter(this, R.layout.row_ping_log, logs);
        logList.setAdapter(adapter);
        logList.setOnScrollListener(this);

        initToolbar(task.getIp());
    }

    @SuppressWarnings("unchecked")
    protected List<L> getFirstPageLogsNewestFirst() {
        return SugarRecord.find(_getLogClass(), "task_parent = ? ORDER BY id DESC LIMIT ?",
                Long.toString(tId), Integer.toString(PAGE_SIZE));
    }

    @SuppressWarnings("unchecked")
    protected List<L> getLogsNewestFirst() {
        return SugarRecord.findWithQuery(_getLogClass(),
                "SELECT * FROM " + NamingHelper.toSQLName(_getLogClass()) +
                        " WHERE task_parent = ? " + "ORDER BY id DESC LIMIT ? " +
                        "OFFSET ?",
                Long.toString(tId), Integer.toString(PAGE_SIZE),
                Long.toString(adapter.getCount()));
    }

    public void onDeleteLogs() {
        SimpleLog.deleteAll(_getLogClass(), "task_parent = ?", Long.toString(tId));
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    protected void clearAndUpdateAdapter() {
        List<L> newLogs = L.find((_getLogClass()), "task_parent = ?",
                new String[]{Long.toString(tId)},
                null, "id DESC", "" + adapter.getCount());
        adapter.clear();
        adapter.addAll(newLogs);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) { }

    int lastItemNumb = 0;
    int preLastItemNumb = 0;
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

    protected void initToolbar(String title) {
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

        sharedMenu = new SharedMenuFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(sharedMenu, SharedMenuFragment.TAG);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_activity_menu, menu);
        return true;
    }

    public void onClearLogs(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SureDialogListener listener = new SureDialogListener();
        builder.setMessage(R.string.are_you_sure).setPositiveButton(R.string.yes, listener)
                .setNegativeButton(R.string.no, listener).show();
    }

    private class SureDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE)
                onDeleteLogs();
        }
    }
}
