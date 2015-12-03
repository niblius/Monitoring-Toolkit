package com.slaruva.sollertiamonitoring.ping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.slaruva.sollertiamonitoring.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PingLogsAdapter extends ArrayAdapter<PingLog> {
    List<PingLog> logs;
    int layoutResourceId;
    PingLogsAdapter(Context context, int layoutResourceId, List<PingLog> logs) {
        super(context, layoutResourceId, logs);
        this.logs = logs;
        this.layoutResourceId = layoutResourceId;
    }

    private static final SimpleDateFormat StandardFormat =
            new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, null);
        }

        PingLog log = logs.get(position);

        TextView logBody = (TextView)convertView.findViewById(R.id.log_body);
        logBody.setText(log.getResponse());
        TextView logDate = (TextView)convertView.findViewById(R.id.log_date);
        logDate.setText(log.getDatetime(StandardFormat));

        return convertView;
    }
}