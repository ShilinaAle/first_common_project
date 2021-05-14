package com.shilina.project_x;

import android.util.Log;

import java.util.Date;

public class OneCall {
    String caller;
    Date callStartTime;
    Date callPlannedTime;

    public OneCall(String caller, Date callStartTime, Date callPlannedTime){
        this.caller = caller;
        this.callStartTime = callStartTime;
        this.callPlannedTime = callPlannedTime;
        Log.i("LOOK HERE: OC", "Call with: " + caller + "\nWas planned: " + callStartTime + "\nWill: " + callPlannedTime);
    }
}
