package com.niblius.mtoolkit;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SharedMenuFragment extends Fragment {
    public static final String TAG = "SharedMenuFragment";
    private boolean showSettingsButton = true;

    public void setSettingsButtonNotVisible() {
        showSettingsButton = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.shared_menu, menu);
        if(!showSettingsButton) {
            MenuItem settings = menu.findItem(R.id.settings);
            settings.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Shared menu item is selected...");
        switch (item.getItemId()) {
            case R.id.faq:
                onFAQ(item);
                return true;
            case R.id.settings:
                onSettings(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onFAQ(MenuItem item) {

    }

    public void onSettings(MenuItem item) {
        Intent i = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
        startActivity(i);
    }
}