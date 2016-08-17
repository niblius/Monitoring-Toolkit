package com.slaruva.sollertiamonitoring.integrity;

import android.annotation.SuppressLint;
import android.content.Context;
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

// TODO trash_can all SuppressLint("DefaultLocale") and make normal l18n

public class IntegrityLogsAdapter extends ArrayAdapter<IntegrityLog> {
    private int layoutResourceId;

    IntegrityLogsAdapter(Context context, int layoutResourceId, List<IntegrityLog> logs) {
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

        IntegrityLog log = getItem(position);

        TextView logDate = (TextView)convertView.findViewById(R.id.log_date);
        logDate.setText(log.getDatetime(StandardFormat));

        TextView response = (TextView)convertView.findViewById(R.id.response);
        response.setText(log.getResponse());

        LinearLayout element = (LinearLayout)convertView.findViewById(R.id.element);
        SimpleLog.State.toColor(log.getState(), element);

        return convertView;
    }
}
