package com.shilina.project_x;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.PixelFormat;
import android.icu.util.GregorianCalendar;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.widget.Toast;
import android.widget.TimePicker;

import java.util.regex.Pattern;

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
    Object parentObject;

    public String phoneNumber;
    public Date callStartTime;

    //Создание управления окном
    public PlanCallLayout(Context context, Object parentObject, String phoneNumber, Date callStartTime) {
        Log.i("LOOK HERE: PCL", "Inflater has been created");
        this.context = context;
        this.parentObject = parentObject;
        this.phoneNumber = phoneNumber;
        if (callStartTime != null) {
            this.callStartTime = callStartTime;
        } else {
            this.callStartTime = Calendar.getInstance().getTime();
        }
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
        isShown = true;

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
                width, height,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,// Говорим, что приложение будет поверх других
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,  // Необходимо для того чтобы TouchEvent'ы в пустой области передавались на другие приложения
                PixelFormat.TRANSLUCENT); // Само окно прозрачное

        windowMan.addView(vgLayout, layPar);
        isShown = true;

        DatePicker datePicker = (DatePicker) vgLayout.findViewById(R.id.bubble_date_picker);
        TimePicker timePicker = vgLayout.findViewById(R.id.bubble_time_picker);
        timePicker.setIs24HourView(true);
        LinearLayout lLayout = vgLayout.findViewById(R.id.bubble_left);
        TextView textToSend = (TextView) vgLayout.findViewById(R.id.bubble_text);

        final long callStartTimeMillis = callStartTime.getTime();
        final long timeToSetMillis[] = new long[1];
        timeToSetMillis[0] = CalendarHandler.getFreeTimeFromCalendar(context, callStartTimeMillis);

        TextView textPhone = (TextView) vgLayout.findViewById(R.id.bubble_phone);
        textPhone.setText(phoneNumber);

        GregorianCalendar c = new GregorianCalendar();
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                timeToSetMillis[0] = c.getTimeInMillis();
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(c.getTimeInMillis(), "dd.MM.y")));
            }
        });

        boolean[] isConfirmed = new boolean[]{false};
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hours, int minutes) {
                c.set(Calendar.HOUR_OF_DAY, hours);
                c.set(Calendar.MINUTE, minutes);
                timeToSetMillis[0] = c.getTimeInMillis();
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(c.getTimeInMillis(), "dd.MM.y HH:mm")));
                isConfirmed[0] = false;
            }
        });

        Button buttonSend = vgLayout.findViewById(R.id.bubble_button_send);
        Button buttonReturn = vgLayout.findViewById(R.id.bubble_button_return);
        if (timeToSetMillis[0] < 0) {
            lLayout.removeView(timePicker);
            textToSend.setText(context.getResources().getString(R.string.textSMS, "[Выберите дату и время]"));
        } else {
            lLayout.removeView(datePicker);
            textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "dd.MM.y HH:mm")));
            String[] hms = CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "HH:mm").split(":");
            timePicker.setHour(Integer.valueOf(hms[0]));
            timePicker.setMinute(Integer.valueOf(hms[1]));
            buttonSend.setText("Отправить");
            buttonReturn.setText("Назад");
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bst = buttonSend.getText().toString();
                if (bst.equals("Далее")) {
                    if (timeToSetMillis[0] < 0) {
                        Toast.makeText(context, "Введите верную дату", Toast.LENGTH_SHORT).show();
                    } else {
                        lLayout.removeView(datePicker);
                        lLayout.addView(timePicker, 1);
                        buttonSend.setText("Отправить");
                        buttonReturn.setText("Назад");
                    }
                    Log.i("LOOK HERE: PCL", "Далее");
                } else if (bst.equals("Отправить")) {
                    Log.i("LOOK HERE: PCL", "Отправить");
                    String numberToSend = textPhone.getText().toString();
                    if (!Pattern.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", numberToSend) || (textToSend.getText().toString().split(":").length != 3)) {
                        Log.i("LOOK HERE: PCL", "Номер или время введены некорректно");
                        Toast.makeText(context, "Введите корректный номер телефона и время", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!CalendarHandler.isFreeAt(context, timeToSetMillis[0]) && !isConfirmed[0]) {
                            Toast.makeText(context, "На выбранное время уже запланировано другое мериприятие.\nНажмите \"Отправить\", чтобы подвердить", Toast.LENGTH_SHORT).show();
                            isConfirmed[0] = true;
                        } else {
                            Runnable backgroundProcess = new Runnable() {
                                public void run() {
                                    try {
                                        HashMap<String, String> data = new HashMap<String, String>() {{
                                            put("email", SettingsActivity.getUser(context));
                                            put("recipient_number", numberToSend);
                                            put("call_datetime", Long.toString(callStartTimeMillis / 1000));
                                            put("callback_datetime", Long.toString(timeToSetMillis[0] / 1000));
                                        }};
                                        ServerHandler addCallQuery = new ServerHandler(ServerHandler.ACTION_SET_RESCHEDULING, data);
                                        addCallQuery.execute();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Thread thread = new Thread(null, backgroundProcess, "Background");
                            thread.start();
                            CalendarHandler.addEvent(context, numberToSend, callStartTimeMillis, timeToSetMillis[0]);
                            SMSHandler.sendSMS(context, numberToSend, textToSend.getText().toString());
                            PhoneHandler.endRingingCall(context, numberToSend);
                            try{
                                ((LaterCallsActivity) parentObject).onResume();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            removePCL();
                        }
                    }
                }
            }
        });

        Button buttonWait15 = vgLayout.findViewById(R.id.bubble_button_wait15);
        buttonWait15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LOOK HERE: PCL", "+15 минут");
                timeToSetMillis[0] = callStartTimeMillis + 900000;
                String[] hms = CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "HH:mm").split(":");
                timePicker.setHour(Integer.valueOf(hms[0]));
                timePicker.setMinute(Integer.valueOf(hms[1]));
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "dd.MM.y HH:mm")));
                if (buttonSend.getText().toString().equals("Далее")) {
                    isConfirmed[0] = false;
                    buttonSend.callOnClick();
                }
                isConfirmed[0] = false;
                buttonSend.callOnClick();
            }
        });

        Button buttonWait30 = vgLayout.findViewById(R.id.bubble_button_wait30);
        buttonWait30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LOOK HERE: PCL", "+30 минут");
                timeToSetMillis[0] = callStartTimeMillis + 1800000;
                String[] hms = CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "HH:mm").split(":");
                timePicker.setHour(Integer.valueOf(hms[0]));
                timePicker.setMinute(Integer.valueOf(hms[1]));
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "dd.MM.y HH:mm")));
                if (buttonSend.getText().toString().equals("Далее")) {
                    isConfirmed[0] = false;
                    buttonSend.callOnClick();
                }
                isConfirmed[0] = false;
                buttonSend.callOnClick();
            }
        });

        Button buttonWait1 = vgLayout.findViewById(R.id.bubble_button_wait1);
        buttonWait1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LOOK HERE: PCL", "+1 час");
                timeToSetMillis[0] = callStartTimeMillis + 3600000;
                String[] hms = CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "HH:mm").split(":");
                timePicker.setHour(Integer.valueOf(hms[0]));
                timePicker.setMinute(Integer.valueOf(hms[1]));
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "dd.MM.y HH:mm")));
                if (buttonSend.getText().toString().equals("Далее")) {
                    isConfirmed[0] = false;
                    buttonSend.callOnClick();
                }
                isConfirmed[0] = false;
                buttonSend.callOnClick();
            }
        });

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String brt = buttonReturn.getText().toString();
                if (brt.equals("Назад")) {
                    lLayout.removeView(timePicker);
                    lLayout.addView(datePicker, 1);
                    buttonSend.setText("Далее");
                    buttonReturn.setText("Отменить");
                    Log.i("LOOK HERE: PCL", "Назад");
                } else if (brt.equals("Отменить")) {
                    removePCL();
                }
            }
        });
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
