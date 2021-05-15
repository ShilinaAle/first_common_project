package com.shilina.project_x;

import androidx.annotation.IntegerRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
    public static ArrayList<OneCall> plannedCallsList = new ArrayList<>();
    LinearLayout oneCallLayout;
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
        textById.setText("Отложенные звонки");

        Log.i("LOOK HERE: LCA", "PCList is: " + plannedCallsList.size());

        oneCallLayout = (LinearLayout) findViewById(R.id.scroll_view_layout);

        //Кнопка "Запланировать звонок"
        View planOneCall = getLayoutInflater().inflate(R.layout.one_call_view, oneCallLayout, false);
        planOneCall.setId(0);
        TextView plannedCallViewText = (TextView) planOneCall.findViewById(R.id.one_call_view_text);
        plannedCallViewText.setText("Чтобы запланировать звонок, нажмите:");
        Button plannedCallViewButton = (Button) planOneCall.findViewById(R.id.one_call_view_button);
        plannedCallViewButton.setText("Запланировать");
        plannedCallViewButton.setTextSize(COMPLEX_UNIT_SP, 8);
        plannedCallViewButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (planLayout == null) {
                    planLayout = new PlanCallLayout(getApplicationContext(),"", Calendar.getInstance().getTime());
                }
                if (!planLayout.isShown) {
                    planLayout.addBubble();
                    //TODO: Назначить закрытие окна кнопкой назад
                }

            }
        });
        oneCallLayout.addView(planOneCall);

        //TODO: Local branch
        if ("admin".equals(SettingsActivity.getUser(getApplicationContext()))) {
            plannedCallsList.add(new OneCall("89998194728-0", new Date(), new Date()));
            plannedCallsList.add(new OneCall("89998194728-1", new Date(), new Date()));
            plannedCallsList.add(new OneCall("89998194728-2", new Date(), new Date()));
            plannedCallsList.add(new OneCall("89998194728-3", new Date(), new Date()));
            plannedCallsList.add(new OneCall("89998194728-4", new Date(), new Date()));
            plannedCallsList.add(new OneCall("89998194728-5", new Date(), new Date()));
            plannedCallsList.add(new OneCall("89998194728-6", new Date(), new Date()));
            plannedCallsList.add(new OneCall("89998194728-7", new Date(), new Date()));
            plannedCallsList.add(new OneCall("89998194728-8", new Date(), new Date()));
            plannedCallsList.add(new OneCall("89998194728-9", new Date(), new Date()));
            Log.i("LOOK HERE: LCA", "poc number is: " + planOneCall.getId());
        }

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

        //TODO: Local branch
        if ("admin".equals(SettingsActivity.getUser(getApplicationContext()))) {
            for (int i = 0; i < plannedCallsList.size(); i++) {
                OneCall poc = plannedCallsList.get(i);
                View nextOneCall = getLayoutInflater().inflate(R.layout.one_call_view, oneCallLayout, false);
                nextOneCall.setId(i + 1);
                TextView oneCallViewText = (TextView) nextOneCall.findViewById(R.id.one_call_view_text);
                oneCallViewText.setText("Звонок с абонентом: " + poc.caller +
                        "\nБыл запланирован: " + CalendarHandler.getTimeStringFromDate(poc.callStartTime, "d.MM.y kk:mm")
                        + "\nБудет: " + CalendarHandler.getTimeStringFromDate(poc.callPlannedTime, "d.MM.y kk:mm"));
                Button oneCallViewButton = (Button) nextOneCall.findViewById(R.id.one_call_view_button);
                oneCallViewButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View removedOneCall = (View) view.getParent();
                        Log.i("LOOK HERE: LCA", "Removing poc number: " + removedOneCall.getId());
                        //TODO: Всплывающее диалоговое окно
                        //TODO: Удаление с сервера
                        //TODO: Удаление из календаря
                        //TODO: Отправка сообщения пользователю?
                        oneCallLayout.removeView(removedOneCall);
                    }
                });
                oneCallLayout.addView(nextOneCall);
            }
        }

        plannedCallsList = new ArrayList<>();
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
                        for (int i = 0; i < plannedCallsArray.length(); i++) {
                            try {
                                JSONObject calli = plannedCallsArray.getJSONObject(Integer.toString(i));
                                String phone = calli.getString("phone");
                                String calldate = calli.getString("call_date_time");
                                String callbackdate = calli.getString("callback_date_time");
                                SimpleDateFormat format = new SimpleDateFormat("d.MM.y kk:mm");
                                Date callDate = format.parse(calldate);
                                Date callbackDate = format.parse(callbackdate);
                                plannedCallsList.add(new OneCall(phone, callDate, callbackDate));
                                CalendarHandler.addEvent(context, phone, callDate.getTime(), callbackDate.getTime());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                for (int i = 0; i < plannedCallsList.size(); i++) {
                                    OneCall poc = plannedCallsList.get(i);
                                    View nextOneCall = getLayoutInflater().inflate(R.layout.one_call_view, oneCallLayout, false);
                                    nextOneCall.setId(i + 1);
                                    TextView oneCallViewText = (TextView) nextOneCall.findViewById(R.id.one_call_view_text);
                                    oneCallViewText.setText("Звонок с абонентом: " + poc.caller +
                                            "\nБыл запланирован: " + CalendarHandler.getTimeStringFromDate(poc.callStartTime, "d.MM.y kk:mm")
                                            + "\nБудет: " + CalendarHandler.getTimeStringFromDate(poc.callPlannedTime, "d.MM.y kk:mm"));
                                    Button oneCallViewButton = (Button) nextOneCall.findViewById(R.id.one_call_view_button);
                                    oneCallViewButton.setOnClickListener(new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            View removedOneCall = (View) view.getParent();
                                            Log.i("LOOK HERE: LCA", "Removing poc number: " + removedOneCall.getId());
                                            //TODO: Всплывающее диалоговое окно
                                            //TODO: Удаление с сервера
                                            //TODO: Удаление из календаря
                                            //TODO: Отправка сообщения пользователю?
                                            oneCallLayout.removeView(removedOneCall);
                                        }
                                    });
                                    oneCallLayout.addView(nextOneCall);
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
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