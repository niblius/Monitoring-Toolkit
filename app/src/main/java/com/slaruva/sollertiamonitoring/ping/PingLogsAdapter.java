package com.slaruva.sollertiamonitoring.ping;

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
public class PingLogsAdapter extends ArrayAdapter<PingLog> {
    private int layoutResourceId;

    PingLogsAdapter(Context context, int layoutResourceId, List<PingLog> logs) {
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

        PingLog log = getItem(position);

        TextView body = (TextView)convertView.findViewById(R.id.log_body);
        body.setText(log.getResponse());
        TextView date = (TextView)convertView.findViewById(R.id.log_date);
        date.setText(log.getDatetime(StandardFormat));
        TextView min = (TextView)convertView.findViewById(R.id.min);
        min.setText("" + log.getMin());
        TextView max = (TextView)convertView.findViewById(R.id.max);
        max.setText("" + log.getMax());
        TextView avg = (TextView)convertView.findViewById(R.id.avg);
        avg.setText("" + log.getAvg());
        TextView mdev = (TextView)convertView.findViewById(R.id.mdev);
        mdev.setText("" + log.getMdev());
        TextView loss = (TextView)convertView.findViewById(R.id.loss);
        loss.setText("" + log.getLoss());
        TextView received = (TextView)convertView.findViewById(R.id.received);
        received.setText("" + log.getReceived());
        TextView transmitted = (TextView)convertView.findViewById(R.id.transmitted);
        transmitted.setText("" + log.getTransmitted());

        LinearLayout element = (LinearLayout)convertView.findViewById(R.id.element);
        if (log.getState() == SimpleLog.State.SUCCESS)
            element.setBackgroundColor(Color.GREEN);
        else if (log.getState() == SimpleLog.State.FAIL)
            element.setBackgroundColor(Color.RED);
        else if (log.getState() == SimpleLog.State.PARTIAL_SUCCESS)
            element.setBackgroundColor(Color.YELLOW);

        return convertView;
    }
}
