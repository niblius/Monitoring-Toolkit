package com.slaruva.sollertiamonitoring.ping;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.TaskScrollableActivity;

import java.util.List;

public class PingActivity extends TaskScrollableActivity<Ping, PingLog> {
    private Ping ping;

    @Override
    protected Class _getLogClass() {
        return PingLog.class;
    }

    @Override
    protected Class _getTaskClass() {
        return Ping.class;
    }

    @Override
    protected ArrayAdapter<PingLog> createAdapter(Context context, int layoutResourceId,
                                                     List<PingLog> logs) {
        return new PingLogsAdapter(context, layoutResourceId, logs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        init();

        ping = (Ping)task;

        setIpToField();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearAndUpdateAdapter();
    }

    @Override
    protected int getLogsLayoutID() {
        return R.layout.row_ping_log;
    }

    public void onSave(View v) {
        EditText ip = (EditText) findViewById(R.id.ip);
        if (!ping.setIp(ip.getText().toString())) {
            errorVisibilityOn();
            return;
        }
        errorVisibilityOff();
        ping.save();
    }
}
