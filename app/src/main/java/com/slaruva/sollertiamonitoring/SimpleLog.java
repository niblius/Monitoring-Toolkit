package com.slaruva.sollertiamonitoring;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;

import com.orm.SugarRecord;

public abstract class SimpleLog extends SugarRecord {
    public enum State {
        SUCCESS,
        FAIL,
        PARTIAL_SUCCESS,
        NOTHING;

        public static State fromInteger(int x) {
            switch (x) {
                case 1:
                    return SUCCESS;
                case 2:
                    return FAIL;
                case 3:
                    return PARTIAL_SUCCESS;
                case 4:
                    return NOTHING;
            }
            return null;
        }

        public static int toInteger(State s) {
            switch (s) {
                case SUCCESS:
                    return 1;
                case FAIL:
                    return 2;
                case PARTIAL_SUCCESS:
                    return 3;
                default:
                    return 4;
            }
        }

        public static String toString(State s, Context cont) {
            switch (s) {
                case SUCCESS:
                    return cont.getString(R.string.success);
                case FAIL:
                    return cont.getString(R.string.fail);
                case PARTIAL_SUCCESS:
                    return cont.getString(R.string.partial_success);
                default:
                    return cont.getString(R.string.error);
            }
        }

        public static void toColor(State s, LinearLayout element) {
            if (s == SimpleLog.State.SUCCESS)
                element.setBackgroundColor(Color.GREEN);
            else if (s == SimpleLog.State.FAIL)
                element.setBackgroundColor(Color.RED);
            else if (s == SimpleLog.State.PARTIAL_SUCCESS)
                element.setBackgroundColor(Color.YELLOW);
        }
    }

    public abstract State getState();
    public abstract void setState(State s);
}
