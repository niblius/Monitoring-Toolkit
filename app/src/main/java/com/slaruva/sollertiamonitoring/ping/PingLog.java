package com.slaruva.sollertiamonitoring.ping;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PingLog extends SugarRecord {
    private Ping taskParent;
    private String response;
    private double min = 0d, avg = 0d, max = 0d, mdev = 0d;
    private int received = 0, transmitted = 0;
    private int loss = 0;

    private long datetime;

    public Ping getTaskParent() {
        return taskParent;
    }

    public void setResponse(String res) {
        this.response = res;
    }

    public String getResponse() {
        return response;
    }

    public int getLoss() {
        return loss;
    }

    public void setLoss(int loss) {
        this.loss = loss;
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

    public long getDatetime() {
        return datetime;
    }

    public String getDatetime(SimpleDateFormat formatter) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);
        return formatter.format(calendar.getTime());
    }

    public PingLog() { }

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

    public double getMdev() {
        return mdev;
    }

    public void setMdev(double mdev) {
        this.mdev = mdev;
    }

    public PingLog(String response, Ping task) {
        this.response = response;
        this.taskParent = task;
        datetime = Calendar.getInstance().getTimeInMillis();
    }

    public PingLog(Ping task) {
        this.taskParent = task;
        datetime = Calendar.getInstance().getTimeInMillis();
    }
}
