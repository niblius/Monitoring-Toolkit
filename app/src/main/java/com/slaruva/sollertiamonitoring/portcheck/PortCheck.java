package com.slaruva.sollertiamonitoring.portcheck;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.SugarRecord;
import com.slaruva.sollertiamonitoring.Helper;
import com.slaruva.sollertiamonitoring.PercentageDisplayer;
import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.SimpleLog;
import com.slaruva.sollertiamonitoring.StatusDisplayer;
import com.slaruva.sollertiamonitoring.Task;
import com.slaruva.sollertiamonitoring.TaskScrollableActivity;
import com.slaruva.sollertiamonitoring.ping.IpDisplayer;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * PortCheck is a task that pings specific port of given server and logs response.
 */
public class PortCheck extends SugarRecord implements Task {
    private String ip;
    private int port;

    //  for performance we use one TelnetClient instance for all
    //  PortChecks in single thread
    private static final ThreadLocal<TelnetClient> client =
            new ThreadLocal<TelnetClient>() {
                @Override
                protected TelnetClient initialValue() { return new TelnetClient(); }
            };

    @Override
    public void execute(Context context) {
        PortCheckLog log = getPortResponse(context);
        log.save();
    }

    /**
     * Performs connection to the server and analyzes response
     * @param context current context
     * @return Resource strings that correspond to: success, error, fail or unknown_host
     */
    private PortCheckLog getPortResponse(Context context) {
        try {
            client.get().connect(ip, port);
            client.get().disconnect();
        } catch (ConnectException ce) {
            return new PortCheckLog(context.getString(R.string.fail),
                    this, SimpleLog.State.FAIL);
        } catch (UnknownHostException e) {
            return new PortCheckLog(context.getString(R.string.unknown_host),
                    this, SimpleLog.State.FAIL);
        } catch (IOException e) {
            return new PortCheckLog(context.getString(R.string.error),
                    this, SimpleLog.State.FAIL);
        }
        return new PortCheckLog(context.getString(R.string.success),
                this, SimpleLog.State.SUCCESS);
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
            holder.img = new StatusDisplayer((ImageView) rowView.findViewById(R.id.state));
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

        holder.img.updateView(this);

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
        i.putExtra(TaskScrollableActivity.TASK_ID_TAG, this.getId().longValue());
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
        return PortCheckLog.count(PortCheckLog.class, "task_parent = ? AND state = ?",
                new String[]{Integer.toString(SimpleLog.State.toInteger(SimpleLog.State.SUCCESS)),
                        this.getId().toString()});
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
        StatusDisplayer img;
        PercentageDisplayer percentage;
    }
}