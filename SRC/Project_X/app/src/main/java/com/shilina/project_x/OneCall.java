package com.shilina.project_x;

import java.util.Date;

public class OneCall {
    String caller;
    Date callStartTime;
    Date callPlannedTime;

    public OneCall(String caller, Date callStartTime){
        this.caller = caller;
        this.callStartTime = callStartTime;
    }

    public void planCallTime(Date callPlannedTime) {
        this.callPlannedTime = callPlannedTime;
    }
}
