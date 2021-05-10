package com.shilina.project_x;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;



public class CalendarHandler {

    public static String phoneNumber;


    public static boolean calendarExist(Context context) {
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uriCal = Calendars.CONTENT_URI;
        uriCal = uriCal.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, "cal@projectx.com")
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.projectx").build();

        boolean checkAC = false;
        String[] projection = new String[] { Calendars.ACCOUNT_NAME, Calendars.ACCOUNT_TYPE};
        cur = cr.query(uriCal, projection, null, null, null);
        while (cur.moveToNext()) {
            String calType = cur.getString(cur.getColumnIndex(Calendars.ACCOUNT_TYPE));
            Log.i("LOOK HERE: CalendarHandler", "\nType: " + calType);
            if (calType.equals("com.projectx")) {
                checkAC = true;
            }
        }
        return checkAC;
    }

    public static void addCalendar(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uriCal = Calendars.CONTENT_URI;
        if (calendarExist(context)) {
            Log.i("LOOK HERE: PhoneStateReceiver", "ProjectX exists");
            return;
        }
        else {
            Log.i("LOOK HERE: PhoneStateReceiver", "ProjectX is to be created");
            ContentValues contentValues = new ContentValues();
            contentValues.put(Calendars.ACCOUNT_NAME, "cal@projectx.com");
            contentValues.put(Calendars.ACCOUNT_TYPE, "com.projectx");
            contentValues.put(Calendars.NAME, "ProjectX Calendar");
            contentValues.put(Calendars.CALENDAR_DISPLAY_NAME, "ProjectX Calendar");
            contentValues.put(Calendars.CALENDAR_COLOR, Color.BLUE);
            contentValues.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
            contentValues.put(Calendars.OWNER_ACCOUNT, "KGrigor98@gmail.com");
            contentValues.put(Calendars.ALLOWED_REMINDERS, "METHOD_ALERT, METHOD_EMAIL, METHOD_ALARM");
            contentValues.put(Calendars.ALLOWED_ATTENDEE_TYPES, "TYPE_OPTIONAL, TYPE_REQUIRED, TYPE_RESOURCE");
            contentValues.put(Calendars.ALLOWED_AVAILABILITY, "AVAILABILITY_BUSY, AVAILABILITY_FREE, AVAILABILITY_TENTATIVE");
            contentValues.put(Calendars.SYNC_EVENTS,1);
            Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
            builder.appendQueryParameter(Calendars.ACCOUNT_NAME, "cal@projectx.com");
            builder.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,"true");
            Uri uri = cr.insert(builder.build(), contentValues);
        }
        cur.close();
    }

    public static String getFreeTimeFromCalendar(Context context, Date startTime) {
        long millis = startTime.getTime();
        long freeTimeMil = -1;

        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = Events.CONTENT_URI;

        String[] from = {
                Events.DTSTART,
                Events.DTEND,
                Events.EVENT_TIMEZONE,
                Events.ALL_DAY
        };
        String where = "((" + Events.DTSTART + " <= ?) AND (" + Events.DTEND + " >= ?))";
        String[] equal = new String[] { Long.toString(millis), Long.toString(millis) };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        cur = cr.query(uri, from, where, equal, null);
        //TODO: Разобраться с часовыми поясами
        while (cur.moveToNext()) {
            Log.i("LOOK HERE: CalendarHandler", "cur is not null");
            long dtend = cur.getLong(cur.getColumnIndex(Events.DTEND));
            if ((dtend >= freeTimeMil) && (cur.getLong(cur.getColumnIndex(Events.ALL_DAY)) == 0)) {
                freeTimeMil = dtend;
            }
        }

        cur.close();

        if (freeTimeMil < 0) {
            //return "Введите время";
            Log.i("LOOK HERE: CalendarHandler", "No current events");
            return null;
        } else {
            //return freeTimeStr;
            Date d = new Date(freeTimeMil);
            if (freeTimeMil != startTime.getTime()){
                String s = getFreeTimeFromCalendar(context, new Date(freeTimeMil));
                d = new Date(Long.parseLong(s));
            }
            Log.i("LOOK HERE: CalendarHandler", "Current event has been found");
            return Long.toString(d.getTime());
        }
    }

    public static String calendarId(Context context) {
        ContentResolver cr = context.getContentResolver();
        String freeTimeId = null;
        Uri uriCal = Calendars.CONTENT_URI;
        uriCal = uriCal.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, "cal@projectx.com")
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.projectx").build();
        Cursor curCal = null;
        String[] projection = new String[] { Calendars._ID, Calendars.ACCOUNT_TYPE};
        curCal = cr.query(uriCal, projection, null, null, null);
        while (curCal.moveToNext()) {
            String calType = curCal.getString(curCal.getColumnIndex(Calendars.ACCOUNT_TYPE));
            Log.i("LOOK HERE: CalendarHandler", "\nName: " + calType);
            if (calType.equals("com.projectx")) {
                freeTimeId = curCal.getString(curCal.getColumnIndex(Calendars._ID));
            }
        }
        curCal.close();
        return freeTimeId;
    }

    public static void addEvent(Context context, String freeTime, String freeTimeTZ, String finalStartTimeStr) {
        ContentResolver cr = context.getContentResolver();
        long freeTimeMil = Long.valueOf(freeTime);
        long freeTimeEnd = freeTimeMil + 300000;
        String freeTimeId = calendarId(context);
        Date finalStartTimeDate = new Date(Long.parseLong(finalStartTimeStr));
        Log.i("LOOK HERE: CalendarHandler", "Call was: " + finalStartTimeStr + "\nCalendarID: " + freeTimeId + "\nTimeZone: " + freeTimeTZ);
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, freeTimeMil);
        values.put(Events.DTEND, freeTimeEnd);
        values.put(Events.TITLE, "Перенесенный звонок");
        values.put(Events.DESCRIPTION, "Вам звонили с номера " + phoneNumber + " в " + toFormat(finalStartTimeDate, "kk:mm"));
        values.put(Events.CALENDAR_ID, freeTimeId);
        values.put(Events.EVENT_TIMEZONE, freeTimeTZ);
        values.put(Events.GUESTS_CAN_INVITE_OTHERS, "1");
        values.put(Events.GUESTS_CAN_SEE_GUESTS, "1");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cr.insert(Events.CONTENT_URI, values);
        Toast.makeText(context, "Напоминание добавлено в календарь", Toast.LENGTH_SHORT).show();
        Log.i("LOOK HERE: CalendarHandler", "Reminder was added to calendar");
    }

    public static String toFormat(Date SomeTimeDate, String p) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(p); //Маска даты
        //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
        dateFormat.setTimeZone(TimeZone.getDefault());
        Log.i("LOOK HERE: CalendarHandler", "Local TimeZone is: " + dateFormat.getTimeZone());
        return dateFormat.format(SomeTimeDate);
    }
}