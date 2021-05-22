package com.shilina.project_x;

import android.net.Uri;
import android.util.Log;

import java.net.URI;
import java.util.Date;

public class OneCall {

    int id;
    String caller;
    Date callStartTime;
    Date callPlannedTime;

    public OneCall(int id, String caller, Date callStartTime, Date callPlannedTime){
        this.id = id;
        this.caller = caller;
        this.callStartTime = callStartTime;
        this.callPlannedTime = callPlannedTime;
        Log.i("LOOK HERE: OC", "callID: " + id + "\nCall with: " + caller + "\nWas planned: " + callStartTime + "\nWill: " + callPlannedTime);
    }
}
