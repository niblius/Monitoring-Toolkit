package com.slaruva.sollertiamonitoring.portcheck;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.slaruva.sollertiamonitoring.Helper;
import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.Task;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;


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
        String resp = getPortResponse(context);
        PortCheckLog log = new PortCheckLog(resp, this);
        log.save();
    }

    /**
     * Performs connection to the server and analyzes response
     * @param context current context
     * @return Resource strings that correspond to: success, error, fail or unknown_host
     */
    private String getPortResponse(Context context) {
        try {
            client.get().connect(ip, port);
            client.get().disconnect();
        } catch (ConnectException ce) {
            return context.getString(R.string.fail);
        } catch (UnknownHostException e) {
            return context.getString(R.string.unknown_host);
        } catch (IOException e) {
            return context.getString(R.string.error);
        }
        return context.getString(R.string.success);
    }

    @Override
    public View getRowView(Context context, View rowView) {
        //TODO colors depending on logs
        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_port_check, null);
        }

        TextView ipView = (TextView)rowView.findViewById(R.id.ip);
        ipView.setText(ip);
        TextView portView = (TextView)rowView.findViewById(R.id.port);
        portView.setText(""+port);

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
