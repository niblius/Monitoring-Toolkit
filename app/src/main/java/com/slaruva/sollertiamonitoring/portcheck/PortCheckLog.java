package com.slaruva.sollertiamonitoring.portcheck;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Represents log for PortCheck task. Contains 3 values:
 * id of parent task;
 * response of the server;
 * time of its creation in milliseconds
*/

public class PortCheckLog extends SugarRecord {
    private PortCheck taskParent;
    private String response;
    private int succeeded;
    private long datetime;

    public static final int SUCCESS = 2;
    public static final int FAIL = 1;
    public int getShortResult() {
        return succeeded;
    }

    public void setSucceeded(int successed) {
        this.succeeded = successed;
    }

    public PortCheck getTaskParent() {
        return taskParent;
    }

    public String getResponse() {
        return response;
    }

    public long getDatetime() {
        return datetime;
    }

    /**
     * Returns datetime in specific format
     * @param formatter particular format in which datetime will be represented
     * @return string of datetime in given format
     */
    public String getDatetime(SimpleDateFormat formatter) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);
        return formatter.format(calendar.getTime());
    }

    public PortCheckLog() { }

    public PortCheckLog(String response, PortCheck task, int succeeded) {
        this.succeeded = succeeded;
        this.response = response;
        this.taskParent = task;
        datetime = Calendar.getInstance().getTimeInMillis();    //TODO i'm not sure about zones...
    }
}
