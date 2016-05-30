package com.slaruva.sollertiamonitoring.portcheck;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.slaruva.sollertiamonitoring.CreateTaskActivity;
import com.slaruva.sollertiamonitoring.R;


/**
 * Activity responsible for creating new tasks of type: PortCheck
 */
public class CreatePortCheckActivity extends CreateTaskActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_port_check);

        initToolbar(savedInstanceState);
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
            errorView.setText(getString(R.string.invalid_port));
            return;
        }

        EditText ipField = (EditText)findViewById(R.id.ip);
        String ip = ipField.getText().toString();

        PortCheck task = new PortCheck();

        if(!task.setPort(port)) {
            errorView.setText(getString(R.string.invalid_port));
            return;
        }
        if(!task.setIp(ip)) {
            errorView.setText(getString(R.string.invalid_ip));
            return;
        }

        task.save();
        this.finish();
    }
}
