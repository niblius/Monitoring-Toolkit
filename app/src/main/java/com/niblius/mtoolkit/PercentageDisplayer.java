package com.niblius.mtoolkit;

import android.annotation.SuppressLint;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PercentageDisplayer implements Displayer{
    private TextView percentage;
    public static final String TAG = "PercentageDisplayer";

    @SuppressLint("DefaultLocale")
    @Override
    public void updateView(Task t) {
        percentage.setText(String.format("%.1f", 100.d*t.countSuccessfulLogs()/(double)t.countAllLogs()));
    }

    public PercentageDisplayer(LinearLayout layout) {
        percentage = (TextView) layout.findViewById(R.id.percentage);
    }
}