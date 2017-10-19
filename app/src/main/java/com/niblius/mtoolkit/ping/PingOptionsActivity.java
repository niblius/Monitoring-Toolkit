package com.niblius.mtoolkit.ping;

import com.niblius.mtoolkit.R;
import com.niblius.mtoolkit.TaskBasicOptionsActivity;

public class PingOptionsActivity extends TaskBasicOptionsActivity<Ping> {
    @Override
    protected int _getLayoutID() {
        return R.layout.activity_ping_options;
    }

    @Override
    protected Class _getTaskClass() {
        return Ping.class;
    }
}