package com.shilina.project_x;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Date;

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

    //Переменные для управления всплывающими окнами
    public static WindowManager windowMan;
    public static LayoutInflater layInflater;
    public static WindowManager.LayoutParams layPar;
    public static ViewGroup callLayout;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("LOOK HERE: PhoneHandler", index++ + " example of PH has been created");

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
    public void onIncomingCallReceived(Context context, String phoneNumber, Date start) {
        Log.i("LOOK HERE: PhoneHandler", "Incoming call has been received");
        createLayout(context);
        addButton(context, start);
    }

    //Обработка начала входящего разговора
    public void onIncomingCallAnswered(Context context, String phoneNumber, Date startTime) {
        Log.i("LOOK HERE: PhoneHandler", "Incoming call has been answered");
        removeButton(); //Удаляем всплывающее окно
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

    //Создание управления окном
    public void createLayout(Context context) {
        windowMan = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); //Получаем сервис управления окном
        layInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Получаем сервис управления макетом

        Log.i("LOOK HERE: PhoneHandler", "Inflater has been created");
    }

    //Метод добавления кнопки
    public void addButton(final Context context, final Date startTime) {
        Log.i("LOOK HERE: PhoneHandler", "Button has been created");
        //Присваиваем картинке обработчик нажатия
        callLayout = (ViewGroup) layInflater.inflate(R.layout.button, null);

        int width = windowMan.getDefaultDisplay().getWidth(); //Ширина дисплея
        int height = windowMan.getDefaultDisplay().getHeight(); //Высота дисплея

        layPar = new WindowManager.LayoutParams( //Задаем параметры отображения
                (int) (width / 4), (int) (height / 4),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,// Говорим, что приложение будет поверх других. В поздних API > 26, данный флаг перенесен на TYPE_APPLICATION_OVERLAY
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,  // Необходимо для того чтобы TouchEvent'ы в пустой области передавались на другие приложения
                PixelFormat.TRANSLUCENT); // Само окно прозрачное

        windowMan.addView(callLayout, layPar);

        ImageView button_img = callLayout.findViewById(R.id.button);
        button_img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O_MR1)
            @Override
            public void onClick(View v) {
                Log.i("LOOK HERE: PhoneHandler", "Button has been touched!");

                removeButton();
                addBubble(context, startTime);
            }
        });
    }

    //Метод удаления кнопки
    public void removeButton() {
        try {
            windowMan.removeView(callLayout); //Удаляем раздутый макет из окна
            Log.i("LOOK HERE: PhoneHandler", "Button has been removed");
        } catch (IllegalArgumentException e) {
            Log.i("LOOK HERE: PhoneHandler", "Button has not found");
        }
    }

    //Метод добавления текстового пузыря
    public void addBubble(final Context context, final Date startTime) {
        Log.i("LOOK HERE: PhoneHandler", "Bubble has been created");
        callLayout = (ViewGroup) layInflater.inflate(R.layout.bubble, null);

        int width = windowMan.getDefaultDisplay().getWidth(); //Ширина дисплея
        int height = windowMan.getDefaultDisplay().getHeight(); //Высота дисплея

        Log.i("LOOK HERE: PhoneHandler", "Ширина\nэкрана: " + width + "\nокна: " + width * 90 / 100);
        Log.i("LOOK HERE: PhoneHandler", "Высота\nэкрана: " + height + "\nокна: " + height * 30 / 100);

        layPar = new WindowManager.LayoutParams( //Задаем параметры отображения
                width * 90 / 100, height * 30 / 100,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,// Говорим, что приложение будет поверх других. В поздних API > 26, данный флаг перенесен на TYPE_APPLICATION_OVERLAY
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,  // Необходимо для того чтобы TouchEvent'ы в пустой области передавались на другие приложения
                PixelFormat.TRANSLUCENT); // Само окно прозрачное

        windowMan.addView(callLayout, layPar);
        final TextView text = callLayout.findViewById(R.id.bubble_text);

 /*       int[] timeToSet_str;
        Button buttonSend = callLayout.findViewById(R.id.bubble_button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                if (timeToSet_str[0] == null) {
                    Toast.makeText(context, "Пожалуйста, выберите время", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    addEvent(context, timeToSet_str[0], freeTimeTZ, finalStartTimeStr);
                    String recipient = phoneNumber;
                    sendSMS(context, recipient, text.getText().toString());
                    //sendTelegram(context, recipient, text.getText().toString());
                    TelecomManager teleMan = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                    teleMan.endCall();
                }
            }
        });
*/
        //TODO: Календарь. Строки 296-521 оригинального кода. Вынести в отдельный класс

    }

    //Метод удаления текстового пузыря
    public void removeBubble() {
        try {
            windowMan.removeView(callLayout); //Удаляем раздутый макет из окна
            Log.i("LOOK HERE: PhoneHandler", "Bubble has been removed");
        } catch (IllegalArgumentException e) {
            Log.i("LOOK HERE: PhoneHandler", "Bubble has not found");
        }
    }

    //Очистка экземпляра
    public void clearAll() {

        //Очищаем экран
        removeButton();
        removeBubble();

        //Восстанавливаем переменные
        index = 0;
        phoneNumber = null;

        windowMan = null;
        layInflater = null;
        layPar = null;
        callLayout = null;

    }
}
