package com.slaruva.sollertiamonitoring.ping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.SugarRecord;
import com.slaruva.sollertiamonitoring.Helper;
import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.SimpleLog;
import com.slaruva.sollertiamonitoring.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.List;


// TODO add specific options (all ping flags)
public class Ping extends SugarRecord implements Task {
    //todo add ping time, %loss and so on
    private String ip;
    private static final String TAG = "Ping";

    @Override
    public void execute(Context context) {
        PingLog log;
        try {
            log = ping();
        } catch (Exception e) {
            log = new PingLog(e.getMessage(), this, SimpleLog.State.FAIL);
        }
        log.save();
    }

    @Override
    public View getRowView(Context context, View rowView) {
        PingViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_ping, null);

            holder = new PingViewHolder();
            holder.ipView = (TextView)rowView.findViewById(R.id.ip);
            holder.element = (RelativeLayout) rowView.findViewById(R.id.element);
            holder.img = (ImageView) rowView.findViewById(R.id.state);
            rowView.setTag(holder);
        } else {
            holder = (PingViewHolder) rowView.getTag();
        }

        holder.ipView.setText(ip);

        List logs = PingLog.find(PingLog.class, "task_parent = ?",
                new String[]{this.getId().toString()},
                null, "id DESC", "1");

        PingLog lastLog = null;
        if(!logs.isEmpty())
            lastLog = (PingLog)logs.get(0);

        if(lastLog == null)
            holder.img.setImageResource(R.drawable.being_processed);
        else if (lastLog.getState() == SimpleLog.State.SUCCESS)
            holder.img.setImageResource(R.drawable.success);
        else if (lastLog.getState() == SimpleLog.State.FAIL)
            holder.img.setImageResource(R.drawable.failed);
        else if (lastLog.getState() == SimpleLog.State.PARTIAL_SUCCESS)
            holder.img.setImageResource(R.drawable.partial_success);

        return rowView;
    }

    public static final String PING_ID = "PING_ID";
    @Override
    public Intent getIntentToDetailedInfo(Context context) {
        Intent i = new Intent(context, PingActivity.class);
        i.putExtra(PING_ID, this.getId().longValue());
        return i;
    }

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

    // modified stackoveflow code
    /**
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

    public static final int numberOfPings = 5;

    public PingLog ping() throws IOException, InterruptedException {
        StringBuffer echo = new StringBuffer();
        Runtime runtime = Runtime.getRuntime();
        Log.i(TAG, "About to ping using runtime.exec");
        Process proc = runtime.exec("ping -c " + numberOfPings + " " + ip);
        proc.waitFor();
        // "unknown host" messsage goes to stderr
        StringBuilder b = buffStream(proc.getInputStream());
        b.append(buffStream(proc.getErrorStream()));

        return getPingLog(b.toString());
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
    public PingLog getPingLog(String s) {
        //  TODO strings/localization
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
}

class PingViewHolder {
    TextView ipView;
    RelativeLayout element;
    ImageView img;
}