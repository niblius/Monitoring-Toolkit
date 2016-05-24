package com.slaruva.sollertiamonitoring;

import android.annotation.SuppressLint;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PercentageDisplayer implements Displayer{
    private TextView percentage;

    @SuppressLint("DefaultLocale")
    @Override
    public void updateView(Task t) {
        percentage.setText(String.format("%.1f", 100.d*t.countSuccessfulLogs()/t.countAllLogs()));
    }

    public PercentageDisplayer(LinearLayout layout) {
        percentage = (TextView) layout.findViewById(R.id.percentage);
    }
}