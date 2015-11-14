package com.slaruva.sollertiamonitoring;


import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


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
    public View toIndexView(Context context) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        TextView address = new TextView(context);
        address.setText(ip + ": " + port);
        container.addView(address);

        LinearLayout logContainer = new LinearLayout(context);
        logContainer.setOrientation(LinearLayout.VERTICAL);
        logContainer.setLeft(5);
        List<PortCheckLog> logs = PortCheckLog.find(PortCheckLog.class, "task_parent = ?",
                new String[]{this.getId().toString()}, null, "ID DESC", "5");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);

        for(PortCheckLog log : logs) {
            TextView tv = new TextView(context);
            tv.setText(log.getDatetime(format) + ": " + log.getResponse());
            logContainer.addView(tv);
        }

        container.addView(logContainer);

        return container;
    }

    public String getIp() {
        return ip;
    }

    private boolean isValidIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (ip.endsWith(".")) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isValidUlr(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public boolean setIp(String ip) {
        if(isValidIP(ip) || isValidUlr(ip)) {
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

    public PortCheck(String ip, int port) throws InvalidParameterException{
        if(!setIp(ip)) {
            throw new InvalidParameterException("Invalid IP.");
        }

        if(!setPort(port)) {
            throw new InvalidParameterException("Invalid port.");
        }
    }

    public PortCheck() { }
}
