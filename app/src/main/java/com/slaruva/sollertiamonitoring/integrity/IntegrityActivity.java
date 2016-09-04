package com.slaruva.sollertiamonitoring.integrity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.TaskBasicActivity;

import java.util.List;

public class IntegrityActivity extends TaskBasicActivity<Integrity, IntegrityLog> {
    @Override
    protected Class _getLogClass() {
        return IntegrityLog.class;
    }

    @Override
    protected Class _getTaskClass() {
        return Integrity.class;
    }

    @Override
    protected ArrayAdapter<IntegrityLog> createAdapter(Context context, int layoutResourceId,
                                                       List<IntegrityLog> logs) {
        return new IntegrityLogsAdapter(context, layoutResourceId, logs);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integrity);
        init(savedInstanceState);
        setIpToField();
        setRegexpToField();

        logList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Integrity response",
                        adapter.getItem(position).getResponse());
                clipboard.setPrimaryClip(clip);

                Toast t = Toast.makeText(getApplicationContext(),
                        getString(R.string.server_responce_copied),
                        Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void setRegexpToField() {
        EditText regexp = (EditText) findViewById(R.id.regexp);
        regexp.setText(((Integrity)task).getRegexp());
    }

    @Override
    protected int getLogsLayoutID() {
        return R.layout.row_integrity_log;
    }

    @Override
    public void onSave(MenuItem item) {
        Integrity integ = ((Integrity)task);
        EditText ip = (EditText) findViewById(R.id.ip);
        if (!integ.setIp(ip.getText().toString())) {
            errorVisibilityOn();
            return;
        }
        EditText regexp = (EditText) findViewById(R.id.regexp);
        if (!integ.setRegexp(regexp.getText().toString())) {
            errorVisibilityOn();
            return;
        }
        errorVisibilityOff();
        integ.save();
    }

    public void onOptions(MenuItem item) {
        Intent i = new Intent(getApplicationContext(), IntegrityOptionsActivity.class);
        i.putExtra(TaskBasicActivity.TASK_ID_TAG, ((Integrity)task).getId().longValue());
        startActivity(i);
    }
}
