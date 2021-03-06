package com.niblius.mtoolkit.ping;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Ping extends SugarRecord implements Task {
    //todo add ping time, %loss and so on
    private static final String TAG = "Ping";

    private String ip;
    private int warningLimit = 1;
    private boolean enabled = true;
    private int numberOfTries = 5;
    private int priority = 10;

    @Override
    public String getExportString() {
        return "ping('" + ip + "', '" + warningLimit + "', '" + enabled + "', '" +
                numberOfTries + "', '" + priority + "');\n";
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
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean execute(Context context) {
        PingLog log;
        try {
            log = ping(context);
        } catch (Exception e) {
            log = new PingLog(e.getMessage(), this, SimpleLog.State.FAIL);
        }
        log.save();
        return log.getState() != SimpleLog.State.FAIL;
    }

    @Override
    public View getRowView(Context context, View rowView, ViewGroup parent) {
        PingViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_ping, null);

            holder = new PingViewHolder();
            holder.ip = new IpDisplayer((TextView) rowView.findViewById(R.id.ip));
            holder.element = (RelativeLayout) rowView.findViewById(R.id.element);
            holder.status = new StatusDisplayer((ImageView) rowView.findViewById(R.id.state),
                    (LinearLayout) rowView.findViewById(R.id.recent_summary));
            holder.percentage = new PercentageDisplayer(
                    (LinearLayout) rowView.findViewById(R.id.percentage_displayer));
            rowView.setTag(holder);
        } else {
            holder = (PingViewHolder) rowView.getTag();
        }

        holder.ip.updateView(this);

        holder.percentage.updateView(this);

        holder.status.updateView(this);
        return rowView;
    }

    @Override
    public SimpleLog getLastLog() {
        List logs = PingLog.find(PingLog.class, "task_parent = ?",
                new String[]{this.getId().toString()},
                null, "id DESC", "1");

        PingLog lastLog = null;
        if(!logs.isEmpty())
            lastLog = (PingLog)logs.get(0);
        return lastLog;
    }

    @Override
    public Intent getIntentToDetailedInfo(Context context) {
        Intent i = new Intent(context, PingActivity.class);
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

    public Ping(String ip) throws InvalidParameterException {
        if(!setIp(ip)) {
            throw new InvalidParameterException("Invalid IP.");
        }
    }

    public Ping() { }

    /**
     * // modified stackoveflow code
     * Ping a host and return an int value of 0 or 1 or 2 0=success, 1=fail, 2=error
     *
     * Does not work in Android emulator and also delay by '1' second if host not pingable
     * In the Android emulator only ping to 127.0.0.1 works
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public int pingHost() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ping -c 1 " + this.ip);
        proc.waitFor();
        return proc.exitValue();
    }

    private SharedPreferences sharedPreferences;

    /*
    public int getNumberOfTries(Context c) {
        // not used now
        if(sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getInt("pref_portcheck_tries", 5);
    }
    */

    public PingLog ping(Context c) throws IOException, InterruptedException {
        StringBuffer echo = new StringBuffer();
        Runtime runtime = Runtime.getRuntime();
        Log.i(TAG, "About to ping using runtime.exe");
        int numberOfTries = getNumberOfTries();
        Process proc = runtime.exec("ping -c " + numberOfTries + " " + ip);
        proc.waitFor();
        // "unknown host" messsage goes to stderr
        StringBuilder b = buffStream(proc.getInputStream());
        b.append(buffStream(proc.getErrorStream()));

        return getPingLog(b.toString(), numberOfTries);
    }

    private StringBuilder buffStream(InputStream is) throws IOException {
        InputStreamReader r = new InputStreamReader(is);
        BufferedReader b = new BufferedReader(r);
        String line;
        StringBuilder echo = new StringBuilder();
        while ((line = b.readLine()) != null) {
            echo.append(line + "\n");
            Log.d(TAG, line);
        }
        return echo;
    }

    /**
     * getPingStats interprets the text result of a Linux ping command
     *
     * Set pingError on error and return null
     *
     * http://en.wikipedia.org/wiki/Ping
     *
     * PING 127.0.0.1 (127.0.0.1) 56(84) ints of data.
     * 64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=0.251 ms
     * 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time=0.294 ms
     * 64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=0.295 ms
     * 64 bytes from 127.0.0.1: icmp_seq=4 ttl=64 time=0.300 ms
     *
     * --- 127.0.0.1 ping statistics ---
     * 4 packets transmitted, 4 received, 0% packet loss, time 0ms
     * rtt min/avg/max/mdev = 0.251/0.285/0.300/0.019 ms
     *
     * PING 192.168.0.2 (192.168.0.2) 56(84) bytes of data.
     *
     * --- 192.168.0.2 ping statistics ---
     * 1 packets transmitted, 0 received, 100% packet loss, time 0ms
     *
     * # ping 321321.
     * ping: unknown host 321321.
     *
     * 1. Check if output contains 0% packet loss : Branch to success -> Get stats
     * 2. Check if output contains 100% packet loss : Branch to fail -> No stats
     * 3. Check if output contains 25% packet loss : Branch to partial success -> Get stats
     * 4. Check if output contains "unknown host"
     *
     * @param s
     */
    private PingLog getPingLog(String s, int numberOfPings) {
        PingLog log = new PingLog(this);
        log.setTransmitted(numberOfPings);
        if (s.contains(" 100% packet loss")) {
            log.setResponse("100% packet loss");
            log.setLoss(100);
            log.setState(SimpleLog.State.FAIL);
        } else if (s.contains("% packet loss")) {
            int start = s.indexOf("/mdev = ");
            int end = s.indexOf(" ms\n", start);
            String statStr = s.substring(start + 8, end);
            String stats[] = statStr.split("/");
            log.setMin(Double.parseDouble(stats[0]));
            log.setAvg(Double.parseDouble(stats[1]));
            log.setMax(Double.parseDouble(stats[2]));
            log.setMdev(Double.parseDouble(stats[3]));

            start = s.indexOf("received, ");
            end = s.indexOf("% packet loss");
            int loss = Integer.parseInt(s.substring(start + 10, end));
            log.setLoss(loss);
            if(loss == 0) {
                log.setResponse("Success");
                log.setState(SimpleLog.State.SUCCESS);
            } else  {
                log.setResponse("Partial packet loss");
                log.setState(SimpleLog.State.PARTIAL_SUCCESS);
            }

            start = s.indexOf("transmitted, ");
            end = s.indexOf(" received");
            log.setReceived(Integer.parseInt(s.substring(start + 13, end)));
        } else if (s.contains("unknown host")) {
            log.setResponse("unknown host");
            log.setState(SimpleLog.State.FAIL);
        } else {
            log.setResponse("Error");
            log.setState(SimpleLog.State.FAIL);
        }
        return log;
    }

    @Override
    public long countSuccessfulLogs() {
        return PingLog.count(PingLog.class, "task_parent = ? AND (state = ? OR state = ?)",
                new String[]{this.getId().toString(), Integer.toString(SimpleLog.State.toInteger(SimpleLog.State.SUCCESS)),
                        Integer.toString(SimpleLog.State.toInteger(SimpleLog.State.PARTIAL_SUCCESS))});
    }

    @Override
    public long countAllLogs() {
        return PingLog.count(PingLog.class, "task_parent = ?",
                new String[]{this.getId().toString()});
    }

    class PingViewHolder {
        IpDisplayer ip;
        PercentageDisplayer percentage;
        RelativeLayout element;
        StatusDisplayer status;
    }

    @Override
    public long countFailedLogs(long datetime) {
        return PingLog.count(PingLog.class, "task_parent = ? AND state = ? AND datetime > ?",
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

        return PingLog.count(PingLog.class, "task_parent = ? AND datetime > ?",
                new String[]{this.getId().toString(),
                        Long.toString(twoDaysAgo)});
    }

    @Override
    public int getNumberOfTries() {
        return numberOfTries;
    }

    @Override
    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }
}