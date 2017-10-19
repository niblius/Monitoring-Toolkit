package com.niblius.mtoolkit.ping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.niblius.mtoolkit.R;
import com.niblius.mtoolkit.TaskBasicActivity;

import java.util.List;

public class PingActivity extends TaskBasicActivity<Ping, PingLog> {
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
        init(savedInstanceState);
        setIpToField();
    }

    @Override
    protected int getLogsLayoutID() {
        return R.layout.row_ping_log;
    }

    @Override
    public void onSave(MenuItem item) {
        Ping ping = (Ping)task;
        EditText ip = (EditText) findViewById(R.id.ip);
        if (!ping.setIp(ip.getText().toString())) {
            errorVisibilityOn();
            return;
        }
        errorVisibilityOff();
        ping.save();
    }

    public void onOptions(MenuItem item) {
        Intent i = new Intent(getApplicationContext(), PingOptionsActivity.class);
        i.putExtra(TaskBasicActivity.TASK_ID_TAG, ((Ping)task).getId().longValue());
        startActivity(i);
    }
}
