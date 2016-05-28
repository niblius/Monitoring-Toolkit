package com.slaruva.sollertiamonitoring;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PercentageDisplayer implements Displayer{
    private TextView percentage;
    public static final String TAG = "PercentageDisplayer";

    @SuppressLint("DefaultLocale")
    @Override
    public void updateView(Task t) {
        Log.i(TAG, "successful: " + t.countSuccessfulLogs() + " of " + t.countAllLogs());
        percentage.setText(String.format("%.1f", 100.d*t.countSuccessfulLogs()/t.countAllLogs()));
    }

    public PercentageDisplayer(LinearLayout layout) {
        percentage = (TextView) layout.findViewById(R.id.percentage);
    }
}