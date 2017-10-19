package com.niblius.mtoolkit.portcheck;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.SugarRecord;
import com.niblius.mtoolkit.Helper;
import com.niblius.mtoolkit.IpDisplayer;
import com.niblius.mtoolkit.PercentageDisplayer;
import com.niblius.mtoolkit.R;
import com.niblius.mtoolkit.SimpleLog;
import com.niblius.mtoolkit.StatusDisplayer;
import com.niblius.mtoolkit.Task;
import com.niblius.mtoolkit.TaskBasicActivity;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Integrity is a task that pings specific port of given server and logs response.
 */
public class PortCheck extends SugarRecord implements Task {
    private String ip;
    private int port;
    private int warningLimit = 1;
    private int numberOfTries = 2;
    private boolean enabled = true;
    private int priority = 10;

    @Override
    public String getExportString() {
        return "portcheck('" + ip + "', '" + port + "', '" + warningLimit + "', '" +
                numberOfTries + "', '" + enabled + "', '" + priority + "');\n";
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getNumberOfTries() {
        return numberOfTries;
    }

    @Override
    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }

    public static final String TAG = "Integrity";

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    //  for performance we use one TelnetClient instance for all
    //  PortChecks in single thread
    private static final ThreadLocal<TelnetClient> client =
            new ThreadLocal<TelnetClient>() {
                @Override
                protected TelnetClient initialValue() {
                    TelnetClient telnet = new TelnetClient();
                    telnet.setDefaultTimeout(50000);
                    return telnet;
                }
            };

    @Override
    public boolean execute(Context context) {
        PortCheckLog log = getPortResponse(context);
        log.save();
        return log.getState() != SimpleLog.State.FAIL;
    }

    private SharedPreferences sharedPreferences;

    /*
    public int getNumberOfTries(Context c) {
        // not used now
        if(sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getInt("pref_ping_tries", 2);
    }
    */

    /**
     * Performs connection to the server and analyzes response
     * @param context current context
     * @return Resource strings that correspond to: success, error, fail or unknown_host
     */
    private PortCheckLog getPortResponse(Context context) {
        int NUMBER_OF_TRIES = getNumberOfTries();
        boolean[] results = new boolean[NUMBER_OF_TRIES];
        double [] delays = new double[NUMBER_OF_TRIES];
        double min = Double.MAX_VALUE, max = 0d, avg = 0d;
        int received = 0;
        long beginning;
        for(int i = 0; i < NUMBER_OF_TRIES; i++) {
            beginning = System.nanoTime();
            try {
                client.get().connect(ip, port);
                client.get().disconnect();
            } catch (IOException e) {
                delays[i] = (int) (System.currentTimeMillis() - beginning);
                results[i] = false;
                continue;
            }
            delays[i] = (double) (System.nanoTime() - beginning) / 1000000.d;
            results[i] = true;
            received++;
        }
        for(int i = 0; i < NUMBER_OF_TRIES; i++) {
            if (results[i]) {
                double current = delays[i];
                avg += current;
                if (current < min)
                    min = current;
                if (current > max)
                    max = current;
            }
        }
        avg /= NUMBER_OF_TRIES;

        SimpleLog.State state = SimpleLog.State.NOTHING;
        for (boolean res : results)
        {
            if (res) {
                if (state == SimpleLog.State.NOTHING)
                    state = SimpleLog.State.SUCCESS;
                else if (state == SimpleLog.State.FAIL)
                    state = SimpleLog.State.PARTIAL_SUCCESS;
            } else {
                if (state == SimpleLog.State.NOTHING)
                    state = SimpleLog.State.FAIL;
                else if (state == SimpleLog.State.SUCCESS)
                    state = SimpleLog.State.PARTIAL_SUCCESS;
            }
        }

        PortCheckLog log = new PortCheckLog(SimpleLog.State.toString(state, context), this, state);

        log.setState(state);
        log.setAvg(avg);
        log.setMax(max);
        log.setMin((min == Double.MAX_VALUE) ? 0 : min);
        log.setReceived(received);
        log.setTransmitted(NUMBER_OF_TRIES);

        return log;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getRowView(Context context, View rowView, ViewGroup parent) {
        PortCheckViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_port_check, parent, false);

            holder = new PortCheckViewHolder();
            holder.ip = new IpDisplayer((TextView)rowView.findViewById(R.id.ip));
            holder.portView = (TextView)rowView.findViewById(R.id.port);
            holder.element = (RelativeLayout) rowView.findViewById(R.id.element);
            holder.status = new StatusDisplayer((ImageView) rowView.findViewById(R.id.state),
                    (LinearLayout) rowView.findViewById(R.id.recent_summary));
            holder.percentage =
                    new PercentageDisplayer(
                            (LinearLayout) rowView.findViewById(R.id.percentage_displayer));
            rowView.setTag(holder);
        } else {
            holder = (PortCheckViewHolder)rowView.getTag();
        }

        holder.ip.updateView(this);

        holder.percentage.updateView(this);

        holder.portView.setText(String.format("%d", port));

        holder.status.updateView(this);

        return rowView;
    }

    @Override
    public SimpleLog getLastLog() {
        List logs = PortCheckLog.find(PortCheckLog.class, "task_parent = ?",
                new String[]{this.getId().toString()},
                null, "id DESC", "1");

        PortCheckLog lastLog = null;
        if(!logs.isEmpty())
            lastLog = (PortCheckLog)logs.get(0);
        return lastLog;
    }

    @Override
    public Intent getIntentToDetailedInfo(Context context) {
        Intent i = new Intent(context, PortCheckActivity.class);
        i.putExtra(TaskBasicActivity.TASK_ID_TAG, this.getId().longValue());
        return i;
    }

    @Override
    public String getIp() {
        return ip;
    }

    public boolean setIp(String ip) {
        if(Helper.isValidIP(ip) || Helper.isValidUlr(ip)) {
            this.ip = ip;
            return true;
        }

        return false;
    }

    public int getPort() {
        return port;
    }

    public static final int MAX_PORT = 65535;
    public static final int MIN_PORT = 1;
    public boolean setPort(int port) {
        if(port < MIN_PORT || port > MAX_PORT) {
            return false;
        }

        this.port = port;
        return true;
    }

    public PortCheck(String ip, int port) throws InvalidParameterException {
        if(!setIp(ip)) {
            throw new InvalidParameterException("Invalid IP.");
        }

        if(!setPort(port)) {
            throw new InvalidParameterException("Invalid port.");
        }
    }

    public PortCheck() { }

    @Override
    public long countSuccessfulLogs() {
        return PortCheckLog.count(PortCheckLog.class, "task_parent = ? AND (state = ? OR state = ?)",
        new String[]{this.getId().toString(), Integer.toString(SimpleLog.State.toInteger(SimpleLog.State.SUCCESS)),
                        Integer.toString(SimpleLog.State.toInteger(SimpleLog.State.PARTIAL_SUCCESS))});
    }

    @Override
    public long countAllLogs() {
        return PortCheckLog.count(PortCheckLog.class, "task_parent = ?",
                new String[]{this.getId().toString()});
    }

    class PortCheckViewHolder {
        IpDisplayer ip;
        TextView portView;
        RelativeLayout element;
        StatusDisplayer status;
        PercentageDisplayer percentage;
    }

    @Override
    public long countFailedLogs(long datetime) {
        return PortCheckLog.count(PortCheckLog.class, "task_parent = ? AND state = ? AND datetime > ?",
                new String[]{this.getId().toString(),
                        Integer.toString(SimpleLog.State.toInteger(SimpleLog.State.FAIL)),
                        Long.toString(datetime)});
    }

    @Override
    public int getWarningLimit() {
        return warningLimit;
    }

    @Override
    public void setWarningLimit(int n) {
        warningLimit = n;
        save();
    }

    @Override
    public long countRecentFailedLogs() {
        long twoDaysAgo = System.currentTimeMillis() -  2 * TimeUnit.DAYS.toMillis(1);
        return  countFailedLogs(twoDaysAgo);
    }

    @Override
    public long countAllRecentLogs() {
        long twoDaysAgo = System.currentTimeMillis() -  2 * TimeUnit.DAYS.toMillis(1);

        return PortCheckLog.count(PortCheckLog.class, "task_parent = ? AND datetime > ?",
                new String[]{this.getId().toString(),
                        Long.toString(twoDaysAgo)});
    }
}
