package com.slaruva.sollertiamonitoring.portcheck;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.slaruva.sollertiamonitoring.Helper;
import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.SimpleLog;
import com.slaruva.sollertiamonitoring.Task;

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

    @Override
    public View getRowView(Context context, View rowView) {
        PortCheckViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_port_check, null);

            holder = new PortCheckViewHolder();
            holder.ipView = (TextView)rowView.findViewById(R.id.ip);
            holder.portView = (TextView)rowView.findViewById(R.id.port);
            holder.element = (RelativeLayout) rowView.findViewById(R.id.element);
            holder.img = (ImageView) rowView.findViewById(R.id.state);
            rowView.setTag(holder);
        } else {
            holder = (PortCheckViewHolder)rowView.getTag();
        }

        holder.ipView.setText(ip);
        holder.portView.setText("" + port);
        List logs = PortCheckLog.find(PortCheckLog.class, "task_parent = ?",
                new String[]{this.getId().toString()},
                null, "id DESC", "1");

        PortCheckLog lastLog = null;
        if(!logs.isEmpty())
            lastLog = (PortCheckLog)logs.get(0);

        if(lastLog == null)
            holder.img.setImageResource(R.drawable.being_processed);
        else if (lastLog.getState() == SimpleLog.State.SUCCESS)
            holder.img.setImageResource(R.drawable.success);
        else if (lastLog.getState() == SimpleLog.State.FAIL)
            holder.img.setImageResource(R.drawable.failed);


        return rowView;
    }

    public static final String PORT_CHECK_ID = "PORT_CHECK_ID";
    @Override
    public Intent getIntentToDetailedInfo(Context context) {
        Intent i = new Intent(context, PortCheckActivity.class);
        i.putExtra(PORT_CHECK_ID, this.getId().longValue());
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

    public int getPort() {
        return port;
    }

    @Ignore
    public static final int MAX_PORT = 65535;
    @Ignore
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
}

class PortCheckViewHolder {
    TextView ipView;
    TextView portView;
    RelativeLayout element;
    ImageView img;
}