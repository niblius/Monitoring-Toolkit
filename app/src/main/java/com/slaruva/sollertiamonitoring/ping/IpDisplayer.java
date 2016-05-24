package com.slaruva.sollertiamonitoring.ping;

import android.widget.TextView;

import com.slaruva.sollertiamonitoring.Displayer;
import com.slaruva.sollertiamonitoring.Task;

public class IpDisplayer implements Displayer {
    public static final int MAX_LENGTH = 15;
    private TextView ip;

    public IpDisplayer(TextView ip) {
        this.ip = ip;
    }

    @Override
    public void updateView(Task t) {
        String str = t.getIp();
        if (str.length() > MAX_LENGTH)
            ip.setText(str.substring(0, MAX_LENGTH) + "...");
        else
            ip.setText(str);
    }
}
