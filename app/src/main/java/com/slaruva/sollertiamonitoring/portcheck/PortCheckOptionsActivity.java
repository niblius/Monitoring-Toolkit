package com.slaruva.sollertiamonitoring.portcheck;

import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.TaskBasicOptionsActivity;

public class PortCheckOptionsActivity extends TaskBasicOptionsActivity<PortCheck> {
    @Override
    protected int _getLayoutID() {
        return R.layout.activity_portcheck_options;
    }

    @Override
    protected Class _getTaskClass() {
        return PortCheck.class;
    }
}