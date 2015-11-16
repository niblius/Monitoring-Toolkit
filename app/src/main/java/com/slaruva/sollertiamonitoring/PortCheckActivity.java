package com.slaruva.sollertiamonitoring;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class PortCheckActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_check);
    }

    //TODO onClick mark viewed + colors
    @Override
    protected void onResume() {
        super.onResume();

        List<PortCheckLog> logs;
        PortCheckLogsAdapter adapter;
        long pcId = getIntent().getExtras().getLong(PortCheck.PORT_CHECK_ID);
        PortCheck pc = PortCheck.findById(PortCheck.class, pcId);

        TextView ip = (TextView)findViewById(R.id.ip);
        ip.setText(pc.getIp());
        TextView port = (TextView)findViewById(R.id.port);
        port.setText("" + pc.getPort());

        ListView logList = (ListView)findViewById(R.id.log_list);
        logs = PortCheckLog.find(PortCheckLog.class, "task_parent = ?", ""+pcId);
        adapter = new PortCheckLogsAdapter(this, R.layout.row_port_check_log, logs);
        logList.setAdapter(adapter);
    }

}
