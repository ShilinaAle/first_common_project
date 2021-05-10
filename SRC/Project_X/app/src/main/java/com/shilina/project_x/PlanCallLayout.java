package com.shilina.project_x;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Insets;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import android.widget.Toast;
import android.widget.TimePicker;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;


public class PlanCallLayout {

    public WindowManager windowMan;
    public LayoutInflater layInflater;
    public WindowManager.LayoutParams layPar;
    public ViewGroup vgLayout;
    public int width;
    public int height;
    public Context context;
    public boolean isShown;

    public static String server = "http://192.168.3.7/?action=singup";
    public static String phoneNumber;

    //Создание управления окном
    public PlanCallLayout(Context context) {
        Log.i("LOOK HERE: PCL", "Inflater has been created");
        this.context = context;
        SettingsActivity.chooseTheme(context);
        windowMan = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); //Получаем сервис управления окном
        layInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Получаем сервис управления макетом

        width = getActivitySize(context)[0]; //Ширина дисплея
        height = getActivitySize(context)[1]; //Высота дисплея


    }

    //Метод добавления кнопки
    public void addButton() {
        Log.i("LOOK HERE: PCL", "Button has been created");
        //Присваиваем картинке обработчик нажатия
        vgLayout = (ViewGroup) layInflater.inflate(R.layout.call_logo_layout, null);

        layPar = new WindowManager.LayoutParams( //Задаем параметры отображения
                (int) (width / 4), (int) (height / 4),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,// Говорим, что приложение будет поверх других
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Необходимо для того чтобы TouchEvent'ы в пустой области передавались на другие приложения
                PixelFormat.TRANSLUCENT); // Само окно прозрачное

        windowMan.addView(vgLayout, layPar);

        ImageView button_img = vgLayout.findViewById(R.id.call_logo_button);
        button_img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O_MR1)
            @Override
            public void onClick(View v) {
                Log.i("LOOK HERE: PCL", "Button has been touched!");
                removePCL();
                addBubble();
            }
        });
    }

    //Метод добавления текстового пузыря
    public void addBubble() {
        Log.i("LOOK HERE: PCL", "Bubble has been created");
        vgLayout = (ViewGroup) layInflater.inflate(R.layout.plan_call_layout, null);

        Log.i("LOOK HERE: PCL", "Ширина\n\tэкрана: " + width + "\n\tокна: " + width * 90 / 100);
        Log.i("LOOK HERE: PCL", "Высота\n\tэкрана: " + height + "\n\tокна: " + height * 30 / 100);

        layPar = new WindowManager.LayoutParams( //Задаем параметры отображения
                width * 90 / 100, height * 30 / 100,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,// Говорим, что приложение будет поверх других
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,  // Необходимо для того чтобы TouchEvent'ы в пустой области передавались на другие приложения
                PixelFormat.TRANSLUCENT); // Само окно прозрачное

        windowMan.addView(vgLayout, layPar);
        isShown = true;

        TimePicker timePicker = vgLayout.findViewById(R.id.bubble_time_picker);
        timePicker.setIs24HourView(true);
        final String[] timeToSet_str = new String[] { " " };
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hours, int minutes) {
                LocalDate today = LocalDate.now();
                LocalDateTime todayStart = today.atStartOfDay();
                long timeToSetMillis = todayStart.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli() + TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes) - TimeZone.getDefault().getRawOffset();
                timeToSet_str[0] = Long.toString(timeToSetMillis);
            }
        });

        Button buttonSend = vgLayout.findViewById(R.id.bubble_button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Отправляем строку из баблтекста
                Date dateSet = new Date(Long.parseLong(timeToSet_str[0]));
                if (CalendarHandler.getFreeTimeFromCalendar(context, dateSet) == null){
                    TimeZone timeZone = TimeZone.getDefault();
                    if (!CalendarHandler.calendarExist(context)) {CalendarHandler.addCalendar(context);}
                    CalendarHandler.addEvent(context, timeToSet_str[0], timeZone.getID(), timeToSet_str[0]);
                    removePCL();
                } else {
                    Toast.makeText(context, "В выбранное время уже запланировано другое мериприятие", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonWait15 = vgLayout.findViewById(R.id.bubble_button_wait15);
        buttonWait15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Формируем строку с временем +15 минут и отправляем
                TimeZone timeZone = TimeZone.getDefault();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 15);
                Date date15 = calendar.getTime();
                if (CalendarHandler.getFreeTimeFromCalendar(context,date15) == null){
                    timeToSet_str[0] = Long.toString(date15.getTime());
                    if (!CalendarHandler.calendarExist(context)) {CalendarHandler.addCalendar(context);}
                    CalendarHandler.addEvent(context, timeToSet_str[0], timeZone.getID(), timeToSet_str[0]);
                    removePCL();
                } else {
                    Toast.makeText(context, "В выбранное время уже запланировано другое мериприятие", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonWait30 = vgLayout.findViewById(R.id.bubble_button_wait30);
        buttonWait30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Формируем строку с временем +30 минут и отправляем
                TimeZone timeZone = TimeZone.getDefault();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 30);
                Date date30 = calendar.getTime();
                if (CalendarHandler.getFreeTimeFromCalendar(context,date30) == null){
                    timeToSet_str[0] = Long.toString(date30.getTime());
                    if (!CalendarHandler.calendarExist(context)) {CalendarHandler.addCalendar(context);}
                    CalendarHandler.addEvent(context, timeToSet_str[0], timeZone.getID(), timeToSet_str[0]);
                    removePCL();
                } else {
                    Toast.makeText(context, "В выбранное время уже запланировано другое мериприятие", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonWait1 = vgLayout.findViewById(R.id.bubble_button_wait1);
        buttonWait1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Формируем строку с временем +1 час и отправляем
                TimeZone timeZone = TimeZone.getDefault();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                Date date1 = calendar.getTime();
                if (CalendarHandler.getFreeTimeFromCalendar(context,date1) == null){
                    timeToSet_str[0] = Long.toString(date1.getTime());
                    if (!CalendarHandler.calendarExist(context)) {CalendarHandler.addCalendar(context);}
                    CalendarHandler.addEvent(context, timeToSet_str[0], timeZone.getID(), timeToSet_str[0]);
                    removePCL();
                } else {
                    Toast.makeText(context, "В выбранное время уже запланировано другое мериприятие", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonReturn = vgLayout.findViewById(R.id.bubble_button_return);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePCL();
            }
        });

        /*Date call = new Date();
        Date callback = new Date(Long.parseLong(timeToSet_str[0]));

        HashMap<String, String> parames = new HashMap<>();
        parames.put("email", SettingsActivity.getUser(context));
        parames.put("recipient_number", tel_in);
        parames.put("call_date", CalendarHandler.toFormat(call, "dd.MM.yy"));
        parames.put("call_time", CalendarHandler.toFormat(call, "kk:mm"));
        parames.put("callback_date", CalendarHandler.toFormat(callback, "dd.MM.yy"));
        parames.put("callback_time", CalendarHandler.toFormat(callback, "kk:mm"));
        try
        {
            SendData SD = new SendData();
            SD.parames = parames;
            SD.server = server;
            SD.action = "set_rescheduling";
            SD.contextt = context;
            SD.execute();
        }
        catch (Exception e)
        {

        }
         */

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

    }

    //Перенос звонка
    public void rescheduleCall() {

    }

    public static int[] getActivitySize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return new int[] {windowMetrics.getBounds().width() - insets.left - insets.right, windowMetrics.getBounds().height() - insets.top - insets.bottom};
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return new int[] {displayMetrics.widthPixels, displayMetrics.heightPixels};
        }
    }

    //Метод удаления текстового пузыря
    public void removePCL() {
        try {
            windowMan.removeView(vgLayout); //Удаляем раздутый макет из окна
            isShown = false;
            Log.i("LOOK HERE: PCL", "PCL has been removed");
        } catch (IllegalArgumentException e) {
            Log.i("LOOK HERE: PCL", "PCL has not found");
        } catch (NullPointerException e) {
            Log.i("LOOK HERE: PCL", "windowMan has not found");
        }
    }
}
