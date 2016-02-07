package com.slaruva.sollertiamonitoring.ping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.slaruva.sollertiamonitoring.R;

import java.util.List;

public class PingActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    public static final int PAGE_SIZE = 64;
    Ping ping;
    List<PingLog> logs;
    PingLogsAdapter adapter;
    long pId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

        pId = getIntent().getExtras().getLong(Ping.PING_ID);
        ping = Ping.findById(Ping.class, pId);

        EditText ip = (EditText)findViewById(R.id.ip);
        ip.setText(ping.getIp());

        ListView logList = (ListView)findViewById(R.id.log_list);
        logs = PingLog.find(PingLog.class, "task_parent = ?",
                new String[]{""+pId},
                null, "id DESC", ""+PAGE_SIZE);

        adapter = new PingLogsAdapter(this, R.layout.row_ping_log, logs);
        logList.setAdapter(adapter);
        logList.setOnScrollListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<PingLog> newLogs = PingLog.find(PingLog.class, "task_parent = ?",
                new String[]{""+pId},
                null, "id DESC", ""+logs.size());
        logs.clear();
        logs.addAll(newLogs);
        adapter.notifyDataSetChanged();
    }

    public void save(View v) {
        EditText ip = (EditText)findViewById(R.id.ip);
        ping.setIp(ip.getText().toString());
        ping.save();
    }

    public void delete(View v) {
        PingLog.deleteAll(PingLog.class, "task_parent = ?", "" + ping.getId());
        ping.delete();
        this.finish();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) { }

    int lastItemNumb = 0;
    int preLastItemNumb = 0;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        lastItemNumb = firstVisibleItem + visibleItemCount;
        if(preLastItemNumb != lastItemNumb && lastItemNumb == totalItemCount) {
            preLastItemNumb = lastItemNumb;
            //  Unfortunately Sugar ORM doesn't have OFFSET in
            //  query builder
            List<PingLog> newLogs = PingLog.findWithQuery(PingLog.class,
                    "SELECT * FROM ping_log WHERE task_parent = ? " +
                    "ORDER BY id DESC LIMIT ? " +
                    "OFFSET ?",
                    ""+pId, ""+PAGE_SIZE, ""+logs.size());
            logs.addAll(newLogs);
            adapter.notifyDataSetChanged();
        }
    }
}
