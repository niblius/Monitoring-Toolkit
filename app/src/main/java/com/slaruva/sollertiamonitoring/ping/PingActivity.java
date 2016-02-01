package com.slaruva.sollertiamonitoring.ping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.slaruva.sollertiamonitoring.R;

import java.util.List;

public class PingActivity extends AppCompatActivity {
    public static int MAXIMUM_SHOWED = 24;

    private Ping pinger;
    List<PingLog> logs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PingLogsAdapter adapter;
        long pcId = getIntent().getExtras().getLong(Ping.PING_ID);
        pinger = Ping.findById(Ping.class, pcId);

        EditText ip = (EditText)findViewById(R.id.ip);
        ip.setText(pinger.getIp());

        ListView logList = (ListView)findViewById(R.id.log_list);
        logs = PingLog.find(PingLog.class, "task_parent = ?",
                new String[]{""+pcId},
                null, "id DESC", ""+MAXIMUM_SHOWED);
        adapter = new PingLogsAdapter(this, R.layout.row_ping_log, logs);
        logList.setAdapter(adapter);
    }

    public void save(View v) {
        EditText ip = (EditText)findViewById(R.id.ip);
        pinger.setIp(ip.getText().toString());
        pinger.save();
    }

    public void delete(View v) {
        PingLog.deleteAll(PingLog.class, "task_parent = ?", ""+pinger.getId());
        pinger.delete();
        this.finish();
    }
}
