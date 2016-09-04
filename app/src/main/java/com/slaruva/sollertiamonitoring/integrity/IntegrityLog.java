package com.slaruva.sollertiamonitoring.integrity;

import com.slaruva.sollertiamonitoring.SimpleLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Represents log for Integrity task. Contains 3 values:
 * id of parent task;
 * response of the server;
 * time of its creation in milliseconds
*/

public class IntegrityLog extends SimpleLog {
    private Integrity taskParent;
    private long datetime;
    private int state;
    private String response = ""; // used only in case of fail

    public IntegrityLog() { }

    public IntegrityLog(String response, Integrity task, State succeeded) {
        setState(succeeded);
        this.response = response;
        this.taskParent = task;
        datetime = Calendar.getInstance().getTimeInMillis();
    }

    public Integrity getTaskParent() {
        return taskParent;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public long getDatetime() {
        return datetime;
    }

    @Override
    public State getState() {
        return State.fromInteger(state);
    }

    @Override
    public void setState(State s) {
        state = State.toInteger(s);
    }
}
