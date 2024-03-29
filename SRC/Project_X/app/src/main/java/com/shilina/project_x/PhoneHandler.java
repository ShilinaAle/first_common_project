package com.shilina.project_x;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PhoneHandler extends BroadcastReceiver {

    //Счет экземпляров
    public static int index = 0;
    public static boolean isRegistered = false;

    //Переменные состояния
    public static int statePre = TelephonyManager.CALL_STATE_IDLE;
    public static int stateCur = TelephonyManager.CALL_STATE_IDLE;

    //Переменные отдельного звонка
    public static String phoneNumber;
    public static Date callStartTime;
    public static boolean isIncoming = false;
    public static PlanCallLayout callLayout;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("LOOK HERE: PhoneHandler", index++ + " example of PH has been created");

        if (!SettingsActivity.isAuthorised(context)){
            return;
        }

        //Определение нового состояния телефона относительно звонка
        String state_str = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        int stateNew = TelephonyManager.CALL_STATE_IDLE;
        if (state_str.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            stateNew = TelephonyManager.CALL_STATE_IDLE;
        } else if (state_str.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            stateNew = TelephonyManager.CALL_STATE_RINGING;
        } else if (state_str.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            stateNew = TelephonyManager.CALL_STATE_OFFHOOK;
        }
        onCallStateChanged(context, intent, stateNew);
    }

    //Метод обработки изменения текущего состояния телефона относительно звонка
    //Входящие вызовы:  при звонке RINGING (1), при ответе OFFHOOK (2), при отклоенении/завершении IDLE (0)
    //Исходящие вызовах: при наборе OFFHOOK (2), при отклонении/завершении IDLE (0)
    public void onCallStateChanged(Context context, Intent intent, int stateNew) {
        Log.i("LOOK HERE: PhoneHandler", "Previous state is: " + statePre + ", Current state is: " + stateCur + ", New state is: " + stateNew);
        if (stateCur == stateNew && stateCur != statePre) {
            switch (stateNew) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //0 > 1 начало входящего вызова
                    phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    callStartTime = Calendar.getInstance().getTime();
                    isIncoming = true;
                    onIncomingCallReceived(context, phoneNumber, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //1 > 2 начало входящего разговора
                    if (isIncoming) {
                        onIncomingCallAnswered(context, phoneNumber, callStartTime);
                        //0 > 2 начало исходящего вызова
                    } else {
                        phoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");//НЕ РАБОТАЕТ!!!
                        callStartTime = new Date();
                        onOutgoingCallStarted(context, phoneNumber, callStartTime);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // 1 > 0 отклонение входящего вызова
                    if (statePre == TelephonyManager.CALL_STATE_RINGING) {
                        onMissedCall(context, phoneNumber, callStartTime);
                        // 2 > 0 окончание входящего разговора
                    } else if (isIncoming) {
                        onIncomingCallEnded(context, phoneNumber, callStartTime, Calendar.getInstance().getTime());
                        // 2 > 0 окончание исходящего разговора/вызова
                    } else {
                        onOutgoingCallEnded(context, phoneNumber, callStartTime, Calendar.getInstance().getTime());
                    }
                    isIncoming = false;
                    break;
            }
            Log.i("LOOK HERE: PhoneHandler", "The phone number is: " + phoneNumber);
        } else {
            Log.i("LOOK HERE: PhoneHandler", "This is mid-state");
        }
        statePre = stateCur;
        stateCur = stateNew;
    }

    //Обработка начала входящего звонка
    public void onIncomingCallReceived(Context context, String phoneNumber, Date startTime) {
        Log.i("LOOK HERE: PhoneHandler", "Incoming call has been received");
        if (SettingsActivity.isAuthorised(context)) {
            SharedPreferences sp_settings = context.getSharedPreferences(SettingsActivity.SP_FILE, Context.MODE_PRIVATE);
            String curModeIn = sp_settings.getString(SettingsActivity.SP_MODE_IN, SettingsActivity.SP_MODES_IN[0]);
            String curModeOut = sp_settings.getString(SettingsActivity.SP_MODE_OUT, SettingsActivity.SP_MODES_OUT[0]);
            boolean isBusyNow = !CalendarHandler.isFreeAt(context, startTime.getTime());

            if (!isBusyNow && (curModeOut.equals(SettingsActivity.SP_MODES_OUT[1]))) {
                Log.i("LOOK HERE: PhoneHandler", "Mode for Out = OFF");
            } else if (isBusyNow && (curModeIn.equals(SettingsActivity.SP_MODES_IN[1]))) {
                Log.i("LOOK HERE: PhoneHandler", "Mode for In = Auto");
                long timeToSetMillis = CalendarHandler.getFreeTimeFromCalendar(context, startTime.getTime());

                Runnable backgroundProcess = new Runnable() {
                    public void run() {
                        try {
                            HashMap<String, String> data = new HashMap<String, String>() {{
                                put("email", SettingsActivity.getUser(context));
                                put("recipient_number", phoneNumber);
                                put("call_datetime", Long.toString(startTime.getTime() / 1000));
                                put("callback_datetime", Long.toString(timeToSetMillis / 1000));
                            }};
                            ServerHandler addCallQuery = new ServerHandler(ServerHandler.ACTION_SET_RESCHEDULING, data);
                            addCallQuery.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(null, backgroundProcess,"Background");
                thread.start();
                CalendarHandler.addEvent(context, phoneNumber, startTime.getTime(), timeToSetMillis);
                String message = context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis, "dd.MM.y HH:mm"));
                SMSHandler.sendSMS(context, phoneNumber, message);
                endRingingCall(context,phoneNumber);
            } else {
                Log.i("LOOK HERE: PhoneHandler", "Mode = Hand");
                callLayout = new PlanCallLayout(context, PhoneHandler.this, phoneNumber, Calendar.getInstance().getTime());
                callLayout.addButton();
            }
        } else {
            Toast.makeText(context, "Пожалуйста, авторизуйтесь в Project_X", Toast.LENGTH_SHORT).show();
        }
    }

    //Обработка начала входящего разговора
    public void onIncomingCallAnswered(Context context, String phoneNumber, Date startTime) {
        Log.i("LOOK HERE: PhoneHandler", "Incoming call has been answered");
        if (callLayout != null) {
            callLayout.removePCL(); //Удаляем всплывающее окно
        }
    }

    //Обработка окончания входящего разговора
    public void onIncomingCallEnded(Context context, String phoneNumber, Date startTime, Date endTime) {
        Log.i("LOOK HERE: PhoneHandler", "Incoming call has been ended");
        clearAll();
    }

    //Обработка начала исходящего звонка/разговора
    public void onOutgoingCallStarted(Context context, String phoneNumber, Date startTime) {
        Log.i("LOOK HERE: PhoneHandler", "Outgoing call has been started/answered");
    }

    //Обработка окончания исходящего разговора
    public void onOutgoingCallEnded(Context context, String phoneNumber, Date startTime, Date endTime) {
        Log.i("LOOK HERE: PhoneHandler", "Outgoing call has been ended");
    }

    //Обработка окончания входящего вызова (сброс)
    public void onMissedCall(Context context, String phoneNumber, Date startTime) {
        Log.i("LOOK HERE: PhoneHandler", "Incoming call has been declined or missed");
        clearAll();
    }

    //Очистка сессии
    public void clearAll() {
        //Восстанавливаем переменные
        index = 0;
        phoneNumber = null;
        callStartTime = null;
        if (callLayout != null) {
            callLayout.removePCL();
            callLayout = null;
        }
    }

    public static void endRingingCall(Context context, String phone){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
            if (phone.equals(phoneNumber) && stateCur == TelephonyManager.CALL_STATE_RINGING) {
                TelecomManager teleMan = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                teleMan.endCall();
                Log.i("LOOK HERE: PCL", "Звонок завершен");
            }
        }
    }
}
