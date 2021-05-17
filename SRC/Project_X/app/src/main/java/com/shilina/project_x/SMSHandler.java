package com.shilina.project_x;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SMSHandler {

    //Метод отправки СМС
    public static void sendSMS(Context context, String recipient, String message) {
        try {
            SmsManager smsMan = SmsManager.getDefault();
            smsMan.sendTextMessage(recipient, null, message, null, null);
            Log.i("LOOK HERE: SMSHandler", "SMS was sent");
        } catch (Exception e) {
            Log.i("LOOK HERE: SMSHandler", "SMS wasn't sent");
        }
    }

}
