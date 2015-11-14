package com.slaruva.sollertiamonitoring;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


//TODO different activities for different tasks
//TODO validation

/**
 * Activity responsible for creating new tasks of type: PortCheck
 */
public class CreatePortCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
    }

    /**
     * Callback method for button "Create", checks validation and
     * indicates what fields are invalid
     * @param view current context
     */
    public void createPortCheck(View view) {
        TextView errorView = (TextView)findViewById(R.id.error_view);
        int port;
        EditText portField = (EditText)findViewById(R.id.port);
        try {
            port = Integer.parseInt(portField.getText().toString());
        } catch (NumberFormatException e) {
            errorView.setText(getString(R.string.invalid_port).toString());
            return;
        }

        EditText ipField = (EditText)findViewById(R.id.ip);
        String ip = ipField.getText().toString();

        PortCheck task = new PortCheck();

        if(!task.setPort(port)) {
            errorView.setText(getString(R.string.invalid_port).toString());
            return;
        }
        if(!task.setIp(ip)) {
            errorView.setText(getString(R.string.invalid_ip).toString());
            return;
        }

        task.save();
        this.finish();
    }
}
