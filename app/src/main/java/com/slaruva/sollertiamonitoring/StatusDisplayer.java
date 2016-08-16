package com.slaruva.sollertiamonitoring;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class StatusDisplayer implements Displayer{
    private ImageView img;
    private TextView summary_text;
    private LinearLayout summary;

    public StatusDisplayer(ImageView img, LinearLayout summary) {
        this.img = img;
        this.summary = summary;
        this.summary_text = (TextView) summary.findViewById(R.id.recent_summary_text);
    }

    @Override
    public void updateView(Task t) {
        SimpleLog lastLog = t.getLastLog();
        BridgeServiceToApp bridge = BridgeServiceToApp.last(BridgeServiceToApp.class);
        if(!t.isEnabled())
            img.setImageResource(R.drawable.stop);
        else {
            if (bridge == null || bridge.isLastSessionSuccessful()) {
                if (lastLog == null)
                    img.setImageResource(R.drawable.being_processed);
                else if (lastLog.getState() == SimpleLog.State.SUCCESS)
                    img.setImageResource(R.drawable.success);
                else if (lastLog.getState() == SimpleLog.State.FAIL)
                    img.setImageResource(R.drawable.failed);
                else if (lastLog.getState() == SimpleLog.State.PARTIAL_SUCCESS)
                    img.setImageResource(R.drawable.partial_success);
            } else {
                img.setImageResource(R.drawable.offline);
            }
        }

        Context cont = summary.getContext();

        long failed = t.countRecentFailedLogs();
        long allLogs = t.countAllRecentLogs();

        int YELLOW = t.getWarningLimit();
        if(failed == 0)
            summary.setVisibility(View.GONE);
        else {
            summary.setVisibility(View.VISIBLE);

            // string recource deletes leading and post spaces
            summary_text.setText(cont.getString(R.string.summary_beginning) + " " +
                    failed + "/" + allLogs + " " +
                    ((failed == 1) ? cont.getString(R.string.summary_ending_single) :
                            cont.getString(R.string.summary_ending_plural)));

            if(failed <= YELLOW)
                summary_text.setBackgroundResource(R.drawable.recent_logs_line_background_warning);
            else
                summary_text.setBackgroundResource(R.drawable.recent_logs_line_background_fail);

            // after resetting background padding drops
            summary_text.setPadding(25, 0, 0, 0);
        }
    }
}
