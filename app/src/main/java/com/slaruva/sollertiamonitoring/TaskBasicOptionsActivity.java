package com.slaruva.sollertiamonitoring;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.orm.SugarRecord;

public abstract class TaskBasicOptionsActivity <T extends SugarRecord & Task> extends AppCompatActivity {
    private String TAG = "TaskBasicOptionsActivity";

    protected Toolbar toolbar;
    protected T task;

    protected EditText tries;
    protected EditText warningLimit;
    protected EditText priority;
    protected Switch on_off;

    private void initToolbar(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        ActionBar mActionBar;
        if ((mActionBar = getSupportActionBar()) != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(_getLayoutID());
        initToolbar(savedInstanceState);

        long tId = getIntent().getExtras().getLong(TaskBasicActivity.TASK_ID_TAG);
        task = (T)T.findById(_getTaskClass(), tId);

        tries = (EditText) findViewById(R.id.tries);
        tries.setText(Integer.toString(task.getNumberOfTries()));
        tries.addTextChangedListener(new OnTriesChangeListener());

        warningLimit = (EditText) findViewById(R.id.warning);
        warningLimit.setText(Integer.toString(task.getWarningLimit()));
        warningLimit.addTextChangedListener(new OnWarningLimitChangeListener());

        on_off = (Switch) findViewById(R.id.is_on);
        on_off.setChecked(task.isEnabled());
        on_off.setOnCheckedChangeListener(new OnEnabledChangeListener());

        priority = (EditText) findViewById(R.id.priority);
        priority.setText(Integer.toString(task.getPriority()));
        priority.addTextChangedListener(new OnPriorityChangeListener());
    }

    class OnPriorityChangeListener implements TextWatcher {
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) { }

        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if(!TextUtils.isEmpty(text)) {
                int val = Integer.valueOf(text);
                task.setPriority(val);
                task.save();
            }
        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) { }
    }

    class OnTriesChangeListener implements TextWatcher {
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) { }

        public void afterTextChanged(Editable s) {
            String text = tries.getText().toString();
            if(!TextUtils.isEmpty(text)) {
                int val = Integer.valueOf(text);
                task.setNumberOfTries(val);
                task.save();
            }
        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) { }
    }

    class OnWarningLimitChangeListener implements TextWatcher {
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) { }

        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if(!TextUtils.isEmpty(text)) {
                int val = Integer.valueOf(text);
                task.setWarningLimit(val);
                task.save();
            }
        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) { }
    }

    class OnEnabledChangeListener implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            task.setEnabled(isChecked);
            task.save();
        }
    }

    protected abstract Class _getTaskClass();
    protected abstract int _getLayoutID();
}