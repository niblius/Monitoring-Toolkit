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
    private double min = 0d, avg = 0d, max = 0d;
    private int received = 0, transmitted = 0;

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public int getTransmitted() {
        return transmitted;
    }

    public void setTransmitted(int transmitted) {
        this.transmitted = transmitted;
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

    @Override
    public State getState() {
        return State.fromInteger(state);
    }

    @Override
    public void setState(State s) {
        state = State.toInteger(s);
    }
}
