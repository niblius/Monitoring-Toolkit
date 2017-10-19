package com.niblius.mtoolkit.ping;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.niblius.mtoolkit.CreateTaskActivity;
import com.niblius.mtoolkit.R;

public class CreatePingActivity extends CreateTaskActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ping);

        initToolbar(savedInstanceState);
    }

    /**
     * Callback method for button "Create", checks validation and
     * indicates what fields are invalid
     * @param view current context
     */
    public void createPing(View view) {
        TextView errorView = (TextView)findViewById(R.id.error_view);

        EditText ipField = (EditText)findViewById(R.id.ip);
        String ip = ipField.getText().toString();

        Ping task = new Ping();

        if(!task.setIp(ip)) {
            errorView.setText(getString(R.string.invalid_ip));
            return;
        }

        task.save();
        this.finish();
    }
}
