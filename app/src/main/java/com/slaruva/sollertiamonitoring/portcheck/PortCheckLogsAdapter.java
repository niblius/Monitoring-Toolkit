package com.slaruva.sollertiamonitoring.portcheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.SimpleLog;
import com.slaruva.sollertiamonitoring.SimpleLogsAdapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PortCheckLogsAdapter extends SimpleLogsAdapter<PortCheckLog> {
    PortCheckLogsAdapter(Context context, int layoutResourceId, List<PortCheckLog> logs) {
        super(context, layoutResourceId, logs);
    }

    @Override
    @SuppressLint("DefaultLocale")
    protected void modifyView(int position, View convertView, ViewGroup parent) {
        PortCheckLog log = getItem(position);

        TextView min = (TextView)convertView.findViewById(R.id.min);
        min.setText(String.format("%.1f", log.getMin()));
        TextView max = (TextView)convertView.findViewById(R.id.max);
        max.setText(String.format("%.1f", log.getMax()));
        TextView avg = (TextView)convertView.findViewById(R.id.avg);
        avg.setText(String.format("%.1f", log.getAvg()));
        TextView received = (TextView)convertView.findViewById(R.id.received);
        received.setText(String.format("%d", log.getReceived()));
        TextView transmitted = (TextView)convertView.findViewById(R.id.transmitted);
        transmitted.setText(String.format("%d", log.getTransmitted()));
    }
}
