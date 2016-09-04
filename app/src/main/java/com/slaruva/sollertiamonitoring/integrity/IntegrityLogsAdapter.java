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
import com.slaruva.sollertiamonitoring.SimpleLogsAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// TODO delete all SuppressLint("DefaultLocale") and make normal l18n

public class IntegrityLogsAdapter extends SimpleLogsAdapter<IntegrityLog> {
    IntegrityLogsAdapter(Context context, int layoutResourceId, List<IntegrityLog> logs) {
        super(context, layoutResourceId, logs);
    }
    @Override
    @SuppressLint("DefaultLocale")
    protected void modifyView(int position, View convertView, ViewGroup parent) {
        IntegrityLog log = getItem(position);

        TextView response = (TextView) convertView.findViewById(R.id.response);
        response.setText(log.getResponse());
    }
}
