

package com.yy.logx;

class LogxModel {

    enum Action {
        WRITE, SEND, FLUSH
    }

    Action action;

    WriteAction writeAction;

    boolean isValid() {
        boolean valid = false;
        if (action != null) {
            if (action == Action.WRITE && writeAction != null && writeAction.isValid()) {
                valid = true;
            } else if (action == Action.FLUSH) {
                valid = true;
            }
        }
        return valid;
    }
}
