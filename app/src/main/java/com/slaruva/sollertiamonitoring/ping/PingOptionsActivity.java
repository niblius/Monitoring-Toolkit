package com.slaruva.sollertiamonitoring.ping;

import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.TaskBasicOptionsActivity;

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