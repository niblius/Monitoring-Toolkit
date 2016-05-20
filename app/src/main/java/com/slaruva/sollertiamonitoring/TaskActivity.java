package com.slaruva.sollertiamonitoring;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

abstract public class TaskActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    protected SharedMenuFragment sharedMenu;

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
                deleteLogs();
        }
    }

    abstract public void deleteLogs();
}
