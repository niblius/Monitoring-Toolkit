package com.slaruva.sollertiamonitoring.ping;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PingLog extends SugarRecord {
    private Ping taskParent;
    private String response;

    private long datetime;

    public Ping getTaskParent() {
        return taskParent;
    }

    public String getResponse() {
        return response;
    }

    public long getDatetime() {
        return datetime;
    }

    public String getDatetime(SimpleDateFormat formatter) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);
        return formatter.format(calendar.getTime());
    }

    public PingLog() { }

    public PingLog(String response, Ping task) {
        this.response = response;
        this.taskParent = task;
        datetime = Calendar.getInstance().getTimeInMillis();    //TODO i'm not sure about zones...
    }
}
