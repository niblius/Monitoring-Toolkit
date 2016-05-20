package com.slaruva.sollertiamonitoring.portcheck;

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

public class PortCheckLogsAdapter extends ArrayAdapter<PortCheckLog> {
    private int layoutResourceId;

    PortCheckLogsAdapter(Context context, int layoutResourceId, List<PortCheckLog> logs) {
        super(context, layoutResourceId, logs);
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

        PortCheckLog log = getItem(position);

        TextView logBody = (TextView)convertView.findViewById(R.id.log_body);
        logBody.setText(log.getResponse());
        TextView logDate = (TextView)convertView.findViewById(R.id.log_date);
        logDate.setText(log.getDatetime(StandardFormat));

        LinearLayout element = (LinearLayout)convertView.findViewById(R.id.element);
        if (log.getState() == SimpleLog.State.SUCCESS)
            element.setBackgroundColor(Color.GREEN);
        else
            element.setBackgroundColor(Color.RED);

        return convertView;
    }
}
