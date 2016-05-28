package com.slaruva.sollertiamonitoring.ping;

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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
// TODO If no logs "logs will be here"
// TODO view holders
public class PingLogsAdapter extends ArrayAdapter<PingLog> {
    private int layoutResourceId;

    PingLogsAdapter(Context context, int layoutResourceId, List<PingLog> logs) {
        super(context, layoutResourceId, logs);
        this.layoutResourceId = layoutResourceId;
    }

    private static final SimpleDateFormat StandardFormat =
            new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
    @Override
    @SuppressLint("DefaultLocale")
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, null);
        }

        PingLog log = getItem(position);

        TextView date = (TextView)convertView.findViewById(R.id.log_date);
        date.setText(log.getDatetime(StandardFormat));
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

        LinearLayout element = (LinearLayout)convertView.findViewById(R.id.element);
        SimpleLog.State.toColor(log.getState(), element);

        return convertView;
    }
}
