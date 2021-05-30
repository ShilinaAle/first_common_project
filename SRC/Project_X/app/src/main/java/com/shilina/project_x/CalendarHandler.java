package com.shilina.project_x;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class CalendarHandler {

    public static boolean isCalendarExists(Context context) {
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uriCal = Calendars.CONTENT_URI;
        uriCal = uriCal.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, "cal@projectx.com")
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.projectx").build();

        String[] projection = new String[] { Calendars.ACCOUNT_NAME, Calendars.ACCOUNT_TYPE};
        cur = cr.query(uriCal, projection, null, null, null);
        while (cur.moveToNext()) {
            String calType = cur.getString(cur.getColumnIndex(Calendars.ACCOUNT_TYPE));
            if (calType.equals("com.projectx")) {
                Log.i("LOOK HERE: CalendarHandler", "Calendar exists");
                cur.close();
                return true;
            }
        }
        cur.close();
        return false;
    }

    public static void addCalendar(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (isCalendarExists(context)) {
            return;
        }
        Log.i("LOOK HERE: CalendarHandler", "ProjectX is to be created");
        ContentResolver cr = context.getContentResolver();
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
        contentValues.put(Calendars.SYNC_EVENTS, 1);
        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(Calendars.ACCOUNT_NAME, "cal@projectx.com");
        builder.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");
        cr.insert(builder.build(), contentValues);
    }

    public static void deleteCalendar(Context context) {
        addCalendar(context);
        ContentResolver cr = context.getContentResolver();
        String calendarId = getCalendarID(context);
        Uri uri = ContentUris.withAppendedId(Calendars.CONTENT_URI, Integer.parseInt(calendarId));
        cr.delete(uri, null, null);
    }

    public static long getFreeTimeFromCalendar(Context context, long currentTime) {
        addCalendar(context);
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
        String[] equal = new String[] { Long.toString(currentTime), Long.toString(currentTime) };

        cur = cr.query(uri, from, where, equal, null);
        while (cur.moveToNext()) {
            long dtend = cur.getLong(cur.getColumnIndex(Events.DTEND));
            if ((dtend >= freeTimeMil) && (cur.getLong(cur.getColumnIndex(Events.ALL_DAY)) == 0)) {
                freeTimeMil = dtend;
            }
        }

        cur.close();

        if (freeTimeMil < 0) {
            Log.i("LOOK HERE: CalendarHandler", "No events at that time");
        } else {
            if (freeTimeMil != currentTime){
                Log.i("LOOK HERE: CalendarHandler", "Сравниваеются " + freeTimeMil + " и " + currentTime);
                freeTimeMil = getFreeTimeFromCalendar(context, freeTimeMil);
            }
            Log.i("LOOK HERE: CalendarHandler", "Current event has been found");
        }
        return freeTimeMil;
    }

    public static boolean isFreeAt(Context context, long timeToCheck){
        return (getFreeTimeFromCalendar(context, timeToCheck) == -1);
    }

    public static String getCalendarID(Context context) {
        ContentResolver cr = context.getContentResolver();
        String calendarID = null;
        Uri uriCal = Calendars.CONTENT_URI;
        uriCal = uriCal.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, "cal@projectx.com")
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.projectx").build();
        String[] projection = new String[] { Calendars._ID, Calendars.ACCOUNT_TYPE};
        Cursor curCal = null;
        curCal = cr.query(uriCal, projection, null, null, null);
        while (curCal.moveToNext()) {
            String calType = curCal.getString(curCal.getColumnIndex(Calendars.ACCOUNT_TYPE));
            Log.i("LOOK HERE: CalendarHandler", "\nName: " + calType);
            if (calType.equals("com.projectx")) {
                calendarID = curCal.getString(curCal.getColumnIndex(Calendars._ID));
            }
        }
        curCal.close();
        return calendarID;
    }

    public static void addEvent(Context context, String phoneNumber, long callStartTime, long freeTime) {
        addCalendar(context);
        if (getEventUri(context, phoneNumber, callStartTime, freeTime) == null) {
            String descr = context.getResources().getString(R.string.textCalendar, phoneNumber, getTimeStringFromLong(callStartTime, "dd.MM.y HH:mm"));
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(Events.DTSTART, freeTime);
            values.put(Events.DTEND, freeTime + 300000);
            values.put(Events.TITLE, "Запланированный звонок");
            values.put(Events.DESCRIPTION, descr);
            values.put(Events.CALENDAR_ID, getCalendarID(context));
            values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            values.put(Events.GUESTS_CAN_INVITE_OTHERS, "1");
            values.put(Events.GUESTS_CAN_SEE_GUESTS, "1");
            values.put(Events.ORGANIZER,"168irishka10@gmail.com");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Uri eventUri = cr.insert(Events.CONTENT_URI, values);
            Log.i("LOOK HERE: CalendarHandler", descr);
            Log.i("LOOK HERE: CalendarHandler", "INSERTED Event ID: " + eventUri.getLastPathSegment());
        }
    }

    public static void deleteEvent(Context context, String phoneNumber, long callStartTime, long freeTime) {
        Uri eventUri = getEventUri(context, phoneNumber, callStartTime, freeTime);
        if (eventUri != null) {
            ContentResolver cr = context.getContentResolver();
            cr.delete(eventUri, null, null);
            Log.i("LOOK HERE: CalendarHandler", "DELETED Event Uri: " + eventUri);
        }
    }

    public static Uri getEventUri(Context context, String phoneNumber, long callStartTime, long freeTime) {
        String descr = context.getResources().getString(R.string.textCalendar, phoneNumber, getTimeStringFromLong(callStartTime, "dd.MM.y HH:mm"));
        ContentResolver cr = context.getContentResolver();
        Uri uri = Events.CONTENT_URI;

        String[] from = {
                Events._ID,
                Events.TITLE,
                Events.DESCRIPTION,
        };
        String where = "((" + Events.DESCRIPTION + " = ?))";
        String[] equal = new String[] { descr };

        long eventID = -1;
        Cursor cur = cr.query(uri, from, where, equal, null);
        while (cur.moveToNext()) {
            eventID = cur.getLong(cur.getColumnIndex(Events._ID));
            Log.i("LOOK HERE: CalendarHandler", "CHECKED Event title: " + cur.getString(cur.getColumnIndex(Events.TITLE)));
            Log.i("LOOK HERE: CalendarHandler", "CHECKED Event ID: " + eventID);
        }
        cur.close();
        if (eventID == -1) {
            Log.i("LOOK HERE: CalendarHandler", "CHECK: new");
            return null;
        } else {
            Log.i("LOOK HERE: CalendarHandler", "CHECK: old");
            return ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
        }
    }

    public static String getTimeStringFromDate(Date SomeTimeDate, String formatStyle) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatStyle); //Маска даты
        //dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(SomeTimeDate);
    }

    public static String getTimeStringFromLong(long millis, String formatStyle) {
        return  getTimeStringFromDate(new Date(millis), formatStyle);
    }
}