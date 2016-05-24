package com.slaruva.sollertiamonitoring.portcheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.TaskScrollableActivity;

import java.util.List;
// TODO ping + portcheck are very similar, copy/paste code fix it!!!

public class PortCheckActivity extends TaskScrollableActivity<PortCheck, PortCheckLog> {
    private PortCheck pc;

    @Override
    protected Class _getLogClass() {
        return PortCheckLog.class;
    }

    @Override
    protected Class _getTaskClass() {
        return PortCheck.class;
    }

    @Override
    protected ArrayAdapter<PortCheckLog> createAdapter(Context context, int layoutResourceId,
                                                     List<PortCheckLog> logs) {
        return new PortCheckLogsAdapter(context, layoutResourceId, logs);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_check);
        init();

        pc = (PortCheck) task;

        setIpToField();
        setPortToField();
    }

    @SuppressLint("DefaultLocale")
    private void setPortToField() {
        EditText port = (EditText) findViewById(R.id.port);
        port.setText(String.format("%d", pc.getPort()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearAndUpdateAdapter();
    }

    public void onSave(View v) {
        // TODO validations in activities
        EditText ip = (EditText) findViewById(R.id.ip);
        if (!pc.setIp(ip.getText().toString())) {
            errorVisibilityOn();
            return;
        }
        EditText port = (EditText) findViewById(R.id.port);
        if (!pc.setPort(Integer.parseInt(port.getText().toString()))) {
            errorVisibilityOn();
            return;
        }
        errorVisibilityOff();
        pc.save();
    }
}
