package com.slaruva.sollertiamonitoring;

import android.widget.ImageView;

public class StatusDisplayer implements Displayer{
    private ImageView img;

    public StatusDisplayer(ImageView img) {
        this.img = img;
    }

    @Override
    public void updateView(Task t) {
        SimpleLog lastLog = t.getLastLog();
        BridgeServiceToApp bridge = BridgeServiceToApp.last(BridgeServiceToApp.class);
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
}
