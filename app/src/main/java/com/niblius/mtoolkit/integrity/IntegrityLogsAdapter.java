package com.niblius.mtoolkit.integrity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.niblius.mtoolkit.R;
import com.niblius.mtoolkit.SimpleLogsAdapter;

import java.util.List;

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
