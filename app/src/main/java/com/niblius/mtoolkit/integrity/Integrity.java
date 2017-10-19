package com.niblius.mtoolkit.integrity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Integrity extends SugarRecord implements Task {
    private String ip;
    private String regexp;
    private int warningLimit = 1;
    private int numberOfTries = 1;
    private boolean enabled = true;
    private int priority = 10;

    @Override
    public String getExportString() {
        return "integrity('" + ip + "', '" + regexp + "', '" + warningLimit + "', '" +
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


    public String getRegexp() {
        return regexp;
    }

    public boolean setRegexp(String regexp) {
        try {
            Pattern.compile(regexp);
        } catch (PatternSyntaxException exception) {
            return false;
        }

        this.regexp = regexp;
        return true;
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

    @Override
    public boolean execute(Context context) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        StringBuilder source = new StringBuilder();

        try {
            url = new URL(ip);
            is = url.openStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                source.append(line);
                source.append("\n");
            }
        } catch (IOException exc) {
            (new IntegrityLog(context.getString(R.string.cant_open_url),
                    this, SimpleLog.State.FAIL)).save();
            Log.e(TAG, exc.toString());
            return false;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // ok. lol
            }
        }

        String page = source.toString();
        String singleLinePage = page.replace("\n", "");

        if (Pattern.matches(regexp, singleLinePage)) {
            (new IntegrityLog(context.getString(R.string.integrity_success_response), this,
                    SimpleLog.State.SUCCESS)).save();
            return true;
        }
        else {
            (new IntegrityLog(page, this, SimpleLog.State.FAIL)).save();
            return false;
        }
    }

    private static final int MAX_LENGTH = 10;

    @SuppressLint("DefaultLocale")
    @Override
    public View getRowView(Context context, View rowView, ViewGroup parent) {
        IntegrityViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_integrity, parent, false);

            holder = new IntegrityViewHolder();
            holder.ip = new IpDisplayer((TextView)rowView.findViewById(R.id.ip));
            holder.regexpView = (TextView)rowView.findViewById(R.id.regexp);
            holder.element = (RelativeLayout) rowView.findViewById(R.id.element);
            holder.status = new StatusDisplayer((ImageView) rowView.findViewById(R.id.state),
                    (LinearLayout) rowView.findViewById(R.id.recent_summary));
            holder.percentage =
                    new PercentageDisplayer(
                            (LinearLayout) rowView.findViewById(R.id.percentage_displayer));
            rowView.setTag(holder);
        } else {
            holder = (IntegrityViewHolder)rowView.getTag();
        }

        holder.ip.updateView(this);
        holder.percentage.updateView(this);
        holder.regexpView.setText(regexp.substring(0, MAX_LENGTH) + "...");
        holder.status.updateView(this);

        return rowView;
    }

    @Override
    public SimpleLog getLastLog() {
        List logs = IntegrityLog.find(IntegrityLog.class, "task_parent = ?",
                new String[]{this.getId().toString()},
                null, "id DESC", "1");

        IntegrityLog lastLog = null;
        if(!logs.isEmpty())
            lastLog = (IntegrityLog)logs.get(0);
        return lastLog;
    }

    @Override
    public Intent getIntentToDetailedInfo(Context context) {
        Intent i = new Intent(context, IntegrityActivity.class);
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

    public Integrity(String ip, String regexp) throws InvalidParameterException {
        if(!setIp(ip)) {
            throw new InvalidParameterException("Invalid IP.");
        }

        if(!setRegexp(regexp)) {
            throw new InvalidParameterException("Invalid regexp.");
        }
    }

    public Integrity() { }

    @Override
    public long countSuccessfulLogs() {
        return IntegrityLog.count(IntegrityLog.class, "task_parent = ? AND (state = ? OR state = ?)",
        new String[]{this.getId().toString(), Integer.toString(SimpleLog.State.toInteger(SimpleLog.State.SUCCESS)),
                        Integer.toString(SimpleLog.State.toInteger(SimpleLog.State.PARTIAL_SUCCESS))});
    }

    @Override
    public long countAllLogs() {
        return IntegrityLog.count(IntegrityLog.class, "task_parent = ?",
                new String[]{this.getId().toString()});
    }

    @Override
    public long countFailedLogs(long datetime) {
        return IntegrityLog.count(IntegrityLog.class, "task_parent = ? AND state = ? AND datetime > ?",
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

        return IntegrityLog.count(IntegrityLog.class, "task_parent = ? AND datetime > ?",
                new String[]{this.getId().toString(),
                        Long.toString(twoDaysAgo)});
    }

    class IntegrityViewHolder {
        IpDisplayer ip;
        TextView regexpView;
        RelativeLayout element;
        StatusDisplayer status;
        PercentageDisplayer percentage;
    }
}
