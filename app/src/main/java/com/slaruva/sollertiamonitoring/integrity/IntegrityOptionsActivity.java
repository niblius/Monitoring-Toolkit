package com.slaruva.sollertiamonitoring.integrity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.TaskBasicOptionsActivity;

public class IntegrityOptionsActivity extends TaskBasicOptionsActivity<Integrity> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView triesTitle = (TextView) findViewById(R.id.tries_title);
        triesTitle.setVisibility(View.GONE);
        tries.setVisibility(View.GONE); // not used now
    }

    @Override
    protected int _getLayoutID() {
        return R.layout.activity_integrity_options;
    }

    @Override
    protected Class _getTaskClass() {
        return Integrity.class;
    }
}