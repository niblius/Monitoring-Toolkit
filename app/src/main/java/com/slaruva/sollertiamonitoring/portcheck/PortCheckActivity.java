package com.slaruva.sollertiamonitoring.portcheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.TaskBasicActivity;

import java.util.List;

public class PortCheckActivity extends TaskBasicActivity<PortCheck, PortCheckLog> {
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
        setContentView(R.layout.activity_portcheck);
        init(savedInstanceState);

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
    protected int getLogsLayoutID() {
        return R.layout.row_port_check_log;
    }

    @Override
    public void onSave(MenuItem item) {
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

    public void onOptions(MenuItem item) {
        Intent i = new Intent(getApplicationContext(), PortCheckOptionsActivity.class);
        i.putExtra(TaskBasicActivity.TASK_ID_TAG, ((PortCheck)task).getId().longValue());
        startActivity(i);
    }
}
