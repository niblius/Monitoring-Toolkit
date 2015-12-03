package com.slaruva.sollertiamonitoring.ping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.slaruva.sollertiamonitoring.R;

import java.util.List;

public class PingActivity extends AppCompatActivity {
    private Ping pinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
    }

    //TODO onClick mark viewed + colors
    @Override
    protected void onResume() {
        super.onResume();

        List<PingLog> logs;
        PingLogsAdapter adapter;
        long pcId = getIntent().getExtras().getLong(Ping.PING_ID);
        pinger = Ping.findById(Ping.class, pcId);

        EditText ip = (EditText)findViewById(R.id.ip);
        ip.setText(pinger.getIp());

        ListView logList = (ListView)findViewById(R.id.log_list);
        logs = PingLog.find(PingLog.class, "task_parent = ?", ""+pcId);
        adapter = new PingLogsAdapter(this, R.layout.row_ping_log, logs);
        logList.setAdapter(adapter);
    }

    public void save(View v) {
        EditText ip = (EditText)findViewById(R.id.ip);
        pinger.setIp(ip.getText().toString());
        pinger.save();
    }

    public void delete(View v) {
        pinger.delete();
        this.finish();
    }
}
