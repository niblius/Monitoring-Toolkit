package com.niblius.mtoolkit;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class CreateTaskActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    protected SharedMenuFragment sharedMenu;

    protected void initToolbar(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.create_new_task);
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
}
