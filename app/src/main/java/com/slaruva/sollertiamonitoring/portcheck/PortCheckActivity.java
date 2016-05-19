package com.slaruva.sollertiamonitoring.portcheck;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.slaruva.sollertiamonitoring.R;

import java.util.List;
// TODO ping + portcheck are very similar, copy/paste code fix it!!!

public class PortCheckActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    public static int PAGE_SIZE = 64;
    PortCheck pc;
    PortCheckLogsAdapter adapter;
    long pcId;
    int lastItemNumb = 0;
    int preLastItemNumb = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_check);

        pcId = getIntent().getExtras().getLong(PortCheck.PORT_CHECK_ID);
        pc = PortCheck.findById(PortCheck.class, pcId);

        EditText ip = (EditText) findViewById(R.id.ip);
        ip.setText(pc.getIp());
        EditText port = (EditText) findViewById(R.id.port);
        port.setText("" + pc.getPort());

        ListView logList = (ListView) findViewById(R.id.log_list);
        List<PortCheckLog> logs = PortCheckLog.find(PortCheckLog.class, "task_parent = ?",
                new String[]{"" + pcId},
                null, "id DESC", "" + PAGE_SIZE);
        adapter = new PortCheckLogsAdapter(this, R.layout.row_port_check_log, logs);
        logList.setAdapter(adapter);
        logList.setOnScrollListener(this);

        initToolbar();
    }

    Toolbar toolbar;

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(pc.getIp());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<PortCheckLog> newLogs = PortCheckLog.find(PortCheckLog.class, "task_parent = ?",
                new String[]{"" + pcId},
                null, "id DESC", "" + adapter.getCount());
        adapter.clear();
        adapter.addAll(newLogs);
        adapter.notifyDataSetChanged();
    }

    public void save(View v) {
        EditText ip = (EditText) findViewById(R.id.ip);
        pc.setIp(ip.getText().toString());
        EditText port = (EditText) findViewById(R.id.port);
        pc.setPort(Integer.parseInt(port.getText().toString()));
        pc.save();
    }

    public void delete(View v) {
        PortCheckLog.deleteAll(PortCheckLog.class, "task_parent = ? ", "" + pc.getId());
        pc.delete();
        this.finish();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        lastItemNumb = firstVisibleItem + visibleItemCount;
        if (preLastItemNumb != lastItemNumb && lastItemNumb == totalItemCount) {
            preLastItemNumb = lastItemNumb;
            //  Unfortunately Sugar ORM doesn't have OFFSET in
            //  query builder
            List<PortCheckLog> newLogs = PortCheckLog.findWithQuery(PortCheckLog.class,
                    "SELECT * FROM port_check_log WHERE task_parent = ? " +
                            "ORDER BY id DESC LIMIT ? " +
                            "OFFSET ?",
                    "" + pcId, "" + PAGE_SIZE, "" + adapter.getCount());
            adapter.addAll(newLogs);
            adapter.notifyDataSetChanged();
        }
    }
}
