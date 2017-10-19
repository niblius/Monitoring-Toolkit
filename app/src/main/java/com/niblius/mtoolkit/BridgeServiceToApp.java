package com.niblius.mtoolkit;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Serves for transporting data from service daemon thread to the application process.
 */
public class BridgeServiceToApp extends SugarRecord {
    private int lastSession;
    private long datetime;

    public String getDatetime(SimpleDateFormat formatter) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);
        return formatter.format(calendar.getTime());
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime() {
        datetime = Calendar.getInstance().getTimeInMillis();
    }

    public boolean isLastSessionSuccessful() {
        return lastSession != 0;
    }

    public void setLastSession(boolean lastSession) {
        this.lastSession = (lastSession) ? 1 : 0;
    }

    public BridgeServiceToApp() {
    }
}
