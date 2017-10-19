package com.niblius.mtoolkit.ping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.niblius.mtoolkit.R;
import com.niblius.mtoolkit.SimpleLogsAdapter;

import java.util.List;

// TODO view holders
public class PingLogsAdapter extends SimpleLogsAdapter<PingLog> {
    PingLogsAdapter(Context context, int layoutResourceId, List<PingLog> logs) {
        super(context, layoutResourceId, logs);
    }

    @Override
    @SuppressLint("DefaultLocale")
    protected void modifyView(int position, View convertView, ViewGroup parent) {
        PingLog log = getItem(position);

        TextView min = (TextView)convertView.findViewById(R.id.min);
        min.setText(String.format("%.1f", log.getMin()));
        TextView max = (TextView)convertView.findViewById(R.id.max);
        max.setText(String.format("%.1f", log.getMax()));
        TextView avg = (TextView)convertView.findViewById(R.id.avg);
        avg.setText(String.format("%.1f", log.getAvg()));
        TextView mdev = (TextView)convertView.findViewById(R.id.mdev);
        mdev.setText(String.format("%.1f", log.getMdev()));
        TextView loss = (TextView)convertView.findViewById(R.id.loss);
        loss.setText(String.format("%d", log.getLoss()));
        TextView received = (TextView)convertView.findViewById(R.id.received);
        received.setText(String.format("%d", log.getReceived()));
        TextView transmitted = (TextView)convertView.findViewById(R.id.transmitted);
        transmitted.setText(String.format("%d", log.getTransmitted()));
    }
}
