package com.niblius.mtoolkit.portcheck;

import com.niblius.mtoolkit.R;
import com.niblius.mtoolkit.TaskBasicOptionsActivity;

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