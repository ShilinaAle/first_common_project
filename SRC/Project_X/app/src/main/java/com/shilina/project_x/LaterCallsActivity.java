package com.shilina.project_x;

import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class LaterCallsActivity extends DrawerActivity {

    public static final String className = Thread.currentThread().getStackTrace()[2].getClassName();
    ViewGroup oneCallLayout;
    public static boolean isAddView = false;
    public static PlanCallLayout planLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_later_calls);
        super.drawerLayout = findViewById(R.id.drawer_layout);
        super.className = className;
        setNickname();
        Log.i("LOOK HERE: LCA", "Opened activity is: " + className);

        //Assign variable
        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Запланированные звонки");

        oneCallLayout = (ViewGroup) findViewById(R.id.scroll_view_layout);

        //Кнопка "Запланировать звонок"
        View planOneCall = getLayoutInflater().inflate(R.layout.one_call_view, oneCallLayout, false);
        planOneCall.setId(0);
        planOneCall.setTag("plan");
        TextView plannedCallViewText = (TextView) planOneCall.findViewById(R.id.one_call_view_text);
        plannedCallViewText.setText("Чтобы запланировать звонок, нажмите:");
        Button plannedCallViewButton = (Button) planOneCall.findViewById(R.id.one_call_view_button);
        plannedCallViewButton.setText("Запланировать");
        plannedCallViewButton.setTextSize(COMPLEX_UNIT_SP, 8);
        plannedCallViewButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (planLayout == null) {
                    planLayout = new PlanCallLayout(getApplicationContext(), LaterCallsActivity.this,"", null);
                }
                if (!planLayout.isShown) {
                    planLayout.addBubble();
                    isAddView = false;
                    //TODO: Назначить закрытие окна кнопкой назад
                }

            }
        });
        oneCallLayout.addView(planOneCall);
        Log.i("LOOK HERE: LCA", "poc number is: " + planOneCall.getId());

        //Вывод всех View в Layout'е
        /*
        for (int j = 0; j < oneCallLayout.getChildCount(); j++) {
            Log.i("LOOK HERE: LCA", "Child: " + oneCallLayout.getChildAt(j));
        }
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        oneCallLayout.removeViews(1, oneCallLayout.getChildCount()-1);

        //Получение звонков
        Runnable backgroundProcess = new Runnable() {
            public void run() {
                try {
                    HashMap<String, String> data = new HashMap<String, String>() {{
                        put("email", SettingsActivity.getUser(getApplicationContext()));
                    }};
                    ServerHandler loginQuery = new ServerHandler(ServerHandler.ACTION_GET_RESCHEDULING, data);
                    loginQuery.execute();
                    String responseString = loginQuery.get();
                    JSONObject responseJSON = new JSONObject(responseString);
                    Context context = getApplicationContext();
                    if (ServerHandler.isErrored(responseString) == null) {
                        JSONObject plannedCallsArray = responseJSON.getJSONObject("calls");
                        for (int i = 1; i <= plannedCallsArray.length(); i++) {
                            try {
                                JSONObject calli = plannedCallsArray.getJSONObject(Integer.toString(i));
                                int id = Integer.parseInt(calli.getString("id"));
                                String phone = calli.getString("phone");
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.y HH:mm");
                                Date callDate = format.parse(calli.getString("call_date_time"));
                                Date callbackDate = format.parse(calli.getString("callback_date_time"));

                                CalendarHandler.addEvent(context, phone, callDate.getTime(), callbackDate.getTime());

                                View nextOneCall = getLayoutInflater().inflate(R.layout.one_call_view, oneCallLayout, false);
                                nextOneCall.setId(i);
                                nextOneCall.setTag("" + id + ":" + callbackDate.getTime());
                                TextView oneCallViewText = (TextView) nextOneCall.findViewById(R.id.one_call_view_text);
                                oneCallViewText.setText(context.getResources().getString(R.string.textOneCall, phone,CalendarHandler.getTimeStringFromDate(callDate,"dd.MM.y HH:mm"),CalendarHandler.getTimeStringFromDate(callbackDate, "dd.MM.y HH:mm")));
                                Button oneCallViewButton = (Button) nextOneCall.findViewById(R.id.one_call_view_button);
                                oneCallViewButton.setOnClickListener(new Button.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        View removedOneCall = (View) view.getParent();
                                        oneCallLayout.removeView(removedOneCall);
                                        Log.i("LOOK HERE: LCA", "Removing poc number: " + removedOneCall.getId());
                                        Runnable backgroundProcess = new Runnable() {
                                            public void run() {
                                                try {
                                                    HashMap<String, String> data = new HashMap<String, String>() {{
                                                        put("id_call", nextOneCall.getTag().toString().split(":")[0]);
                                                    }};
                                                    ServerHandler addCallQuery = new ServerHandler(ServerHandler.ACTION_DELETE_CALL, data);
                                                    addCallQuery.execute();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        Thread thread = new Thread(null, backgroundProcess, "Background");
                                        thread.start();
                                        CalendarHandler.deleteEvent(context, phone, callDate.getTime(), callbackDate.getTime());
                                        String message = context.getResources().getString(R.string.cancelSMS, callDate);
                                        SMSHandler.sendSMS(context, phone, message);
                                    }
                                });
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    public void run() {
                                        oneCallLayout.addView(nextOneCall);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                try {
                                    Toast.makeText(context, "Данные не загружены: " + responseJSON.getString("error_text"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(null, backgroundProcess,"Background");
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closePCL();

    }
    
    @Override
    public void openDrawer(DrawerLayout drawerLayout) {
        super.openDrawer(drawerLayout);
        closePCL();
    }

    static void closePCL(){
        if (planLayout != null) {
            planLayout.removePCL();
            planLayout = null;
        }
    }
}