package com.slaruva.sollertiamonitoring;

import com.orm.SugarRecord;

public class SimpleLog extends SugarRecord {
    public enum State {
        SUCCESS,
        FAIL,
        PARTIAL_SUCCESS;

        public static State fromInteger(int x) {
            switch (x) {
                case 1:
                    return SUCCESS;
                case 2:
                    return FAIL;
                case 3:
                    return PARTIAL_SUCCESS;
            }
            return null;
        }

        public static int toInteger(State s) {
            switch (s) {
                case SUCCESS:
                    return 1;
                case FAIL:
                    return 2;
                default:
                    return 3;
            }
        }
    }
}
