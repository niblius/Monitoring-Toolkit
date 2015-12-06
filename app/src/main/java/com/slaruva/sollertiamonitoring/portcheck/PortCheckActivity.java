package com.slaruva.sollertiamonitoring.portcheck;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.slaruva.sollertiamonitoring.R;

import java.util.List;

public class PortCheckActivity extends AppCompatActivity {
    private PortCheck pc;

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
        pc = PortCheck.findById(PortCheck.class, pcId);

        EditText ip = (EditText)findViewById(R.id.ip);
        ip.setText(pc.getIp());
        EditText port = (EditText)findViewById(R.id.port);
        port.setText("" + pc.getPort());

        ListView logList = (ListView)findViewById(R.id.log_list);
        logs = PortCheckLog.find(PortCheckLog.class, "task_parent = ?", ""+pcId);
        adapter = new PortCheckLogsAdapter(this, R.layout.row_port_check_log, logs);
        logList.setAdapter(adapter);
    }

    public void save(View v) {
        EditText ip = (EditText)findViewById(R.id.ip);
        pc.setIp(ip.getText().toString());
        EditText port = (EditText)findViewById(R.id.port);
        pc.setPort(Integer.parseInt(port.getText().toString()));
        pc.save();
    }

    public void delete(View v) {
        PortCheck.deleteAll(PortCheck.class, "task_parent = ?", "" +pc.getId());
        pc.delete();
        this.finish();
    }
}
