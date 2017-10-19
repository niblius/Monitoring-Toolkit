package com.niblius.mtoolkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public abstract class SimpleLogsAdapter<T extends SimpleLog> extends ArrayAdapter<T> {
    protected int layoutResourceId;
    protected Calendar calendar;

    public SimpleLogsAdapter(Context context, int layoutResourceId, List<T> logs) {
        super(context, layoutResourceId, logs);
        this.layoutResourceId = layoutResourceId;
        calendar = Calendar.getInstance();
    }

    protected abstract void modifyView(int position, View converView, ViewGroup parent);

    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("EEE, MMM d, ''yy", Locale.US);
    private static final SimpleDateFormat timeFormat =
            new SimpleDateFormat("h:mm a", Locale.US);
    @Override
    @SuppressLint("DefaultLocale")
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, null);
        }
        T log = getItem(position);

        LinearLayout element = (LinearLayout)convertView.findViewById(R.id.element);
        SimpleLog.State.toColor(log.getState(), element);

        TextView logTime = (TextView)convertView.findViewById(R.id.time);
        logTime.setText(log.getDatetime(timeFormat));

        int log_day = 0, previous_log_day = 0;
        if(position != 0) {
            calendar.setTimeInMillis(log.getDatetime());
            log_day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.setTimeInMillis(getItem(position - 1).getDatetime());
            previous_log_day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        LinearLayout date_container = (LinearLayout)convertView.findViewById(R.id.day_header);
        if(position == 0 || log_day != previous_log_day) {
            date_container.setVisibility(View.VISIBLE);
            TextView logDate = (TextView)convertView.findViewById(R.id.log_date);
            logDate.setText(log.getDatetime(dateFormat));
        }
        else
            date_container.setVisibility(View.GONE);

        modifyView(position, convertView, parent);

        return convertView;
    }
}
