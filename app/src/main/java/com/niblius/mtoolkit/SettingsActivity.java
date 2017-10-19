package com.niblius.mtoolkit;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedMenuFragment sharedMenu;
    private void initToolbar(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
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
            sharedMenu.setSettingsButtonNotVisible();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(sharedMenu, SharedMenuFragment.TAG);
            transaction.commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initToolbar(savedInstanceState);
        if(savedInstanceState == null)
            addPreferenceFragment();
    }

    private void addPreferenceFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.preference_frame, new MyPreferenceFragment())
                .commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
