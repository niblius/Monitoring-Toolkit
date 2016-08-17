package com.slaruva.sollertiamonitoring.integrity;

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

public class IntegrityActivity extends TaskBasicActivity<Integrity, IntegrityLog> {
    private Integrity integ;

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

        integ = (Integrity) task;

        setIpToField();
        setRegexpToField();
    }

    @SuppressLint("DefaultLocale")
    private void setRegexpToField() {
        EditText regexp = (EditText) findViewById(R.id.regexp);
        regexp.setText(integ.getRegexp());
    }

    @Override
    protected int getLogsLayoutID() {
        return R.layout.row_integrity_log;
    }

    @Override
    public void onSave(MenuItem item) {
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
