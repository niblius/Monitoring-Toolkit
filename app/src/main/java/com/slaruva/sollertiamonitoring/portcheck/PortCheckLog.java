package com.slaruva.sollertiamonitoring.portcheck;

import com.slaruva.sollertiamonitoring.SimpleLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Represents log for PortCheck task. Contains 3 values:
 * id of parent task;
 * response of the server;
 * time of its creation in milliseconds
*/

public class PortCheckLog extends SimpleLog {
    private PortCheck taskParent;
    private String response;
    private long datetime;
    private int state;

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

    public PortCheckLog(String response, PortCheck task, State succeeded) {
        setState(succeeded);
        this.response = response;
        this.taskParent = task;
        datetime = Calendar.getInstance().getTimeInMillis();    //TODO i'm not sure about zones...
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

    public State getState() {
        return State.fromInteger(state);
    }

    public void setState(State s) {
        state = State.toInteger(s);
    }
}
