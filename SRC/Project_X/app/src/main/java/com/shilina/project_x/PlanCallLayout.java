package com.shilina.project_x;

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

    public String phoneNumber;
    public Date callStartTime;

    //Создание управления окном
    public PlanCallLayout(Context context, String phoneNumber, Date callStartTime) {
        Log.i("LOOK HERE: PCL", "Inflater has been created");
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.callStartTime = callStartTime;
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
                width * 90 / 100, height * 30 / 100,
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
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "d.MM.y")));
            }
        });

        boolean[] isConfirmed = new boolean[]{false};
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hours, int minutes) {
                c.set(Calendar.HOUR_OF_DAY, hours);
                c.set(Calendar.MINUTE, minutes);
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(c.getTimeInMillis(), "d.MM.y kk:mm")));
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
            textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "d.MM.y kk:mm")));
            String[] hms = CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "kk:mm").split(":");
            timePicker.setHour(Integer.valueOf(hms[0]) == 24 ? 0 : Integer.valueOf(hms[0]));
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
                    if (!Pattern.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", numberToSend) || (timeToSetMillis[0] < 0)) {
                        Log.i("LOOK HERE: PCL", "Номер или время введены некорректно");
                        Toast.makeText(context, "Введите корректный номер телефона и время", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!CalendarHandler.isFreeAt(context, timeToSetMillis[0]) && !isConfirmed[0]) {
                            Toast.makeText(context, "На выбранное время уже запланировано другое мериприятие.\nНажмите \"Отправить\", чтобы подвердить", Toast.LENGTH_SHORT).show();
                            isConfirmed[0] = true;
                        } else {
                            CalendarHandler.addEvent(context, numberToSend, callStartTimeMillis, timeToSetMillis[0]);
                            Runnable backgroundProcess = new Runnable() {
                                public void run() {
                                    try {
                                        HashMap<String, String> data = new HashMap<String, String>() {{
                                            put("email", SettingsActivity.getUser(context));
                                            put("recipient_number", numberToSend);
                                            put("call_date", CalendarHandler.getTimeStringFromLong(callStartTimeMillis, "d.MM.y"));
                                            put("call_time", CalendarHandler.getTimeStringFromLong(callStartTimeMillis, "kk:mm"));
                                            put("callback_date", CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "d.MM.y"));
                                            put("callback_time", CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "kk:mm"));
                                        }};
                                        ServerHandler addCallQuery = new ServerHandler(ServerHandler.ACTION_SET_RESCHEDULING, data);
                                        addCallQuery.execute();
                                        SMSHandler.sendSMS(context, numberToSend, textToSend.getText().toString());
                                    } catch (Exception e) {
                                    }
                                }
                            };
                            Thread thread = new Thread(null, backgroundProcess, "Background");
                            thread.start();
                            PhoneHandler.endRingingCall(context, numberToSend);
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
                Log.i("LOOK HERE: PCL", "+15 миинут");
                timeToSetMillis[0] = callStartTimeMillis + 900000;
                String[] hms = CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "kk:mm").split(":");
                timePicker.setHour(Integer.valueOf(hms[0]) == 24 ? 0 : Integer.valueOf(hms[0]));
                timePicker.setMinute(Integer.valueOf(hms[1]));
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "d.MM.y kk:mm")));
                isConfirmed[0] = false;
                buttonSend.callOnClick();
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
                String[] hms = CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "kk:mm").split(":");
                timePicker.setHour(Integer.valueOf(hms[0]) == 24 ? 0 : Integer.valueOf(hms[0]));
                timePicker.setMinute(Integer.valueOf(hms[1]));
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "d.MM.y kk:mm")));
                isConfirmed[0] = false;
                buttonSend.callOnClick();
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
                String[] hms = CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "kk:mm").split(":");
                timePicker.setHour(Integer.valueOf(hms[0]) == 24 ? 0 : Integer.valueOf(hms[0]));
                timePicker.setMinute(Integer.valueOf(hms[1]));
                textToSend.setText(context.getResources().getString(R.string.textSMS, CalendarHandler.getTimeStringFromLong(timeToSetMillis[0], "d.MM.y kk:mm")));
                isConfirmed[0] = false;
                buttonSend.callOnClick();
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
