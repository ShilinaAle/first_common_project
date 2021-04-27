package com.shilina.project_x;

import java.util.Date;

public class OneCall {
    String caller;
    Date callStartRime;
    Date callPlannedTime;

    public OneCall(String caller, Date callStartRime){
        this.caller = caller;
        this.callStartRime = callStartRime;
    }

    public void planCallTime(Date callPlannedTime) {
        this.callPlannedTime = callPlannedTime;
    }
}
