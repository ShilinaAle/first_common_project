package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class StatusActivity extends DrawerActivity {

    public static final String className = Thread.currentThread().getStackTrace()[2].getClassName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        if (!SettingsActivity.isPremium(getApplicationContext())) {
            setContentView(R.layout.activity_status);
        } else {
            setContentView(R.layout.activity_status_prem);
        }

        super.drawerLayout = findViewById(R.id.drawer_layout);
        super.className = className;
        setNickname();
        Log.i("LOOK HERE: SA", "Opened activity is: " + className);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Мой статус");
    }

    public void onPurchaseClick(View view) {
        Log.i("LOOK HERE: SA", "Purchased premium ");
        SettingsActivity.setPremium(this, true);

        Runnable backgroundProcess = new Runnable() {
            public void run() {
                try {
                    Context context = getApplicationContext();
                    HashMap<String, String> data = new HashMap<String, String>() {{
                        put("email", SettingsActivity.getUser(context));
                        put("summ", "100");
                        put("pay_date", CalendarHandler.getTimeStringFromDate(Calendar.getInstance().getTime(), "dd.MM.yy"));
                        put("pay_time", CalendarHandler.getTimeStringFromDate(Calendar.getInstance().getTime(), "kk:mm"));
                    }};
                    ServerHandler premiumQuery = new ServerHandler(ServerHandler.ACTION_SET_PREMIUM, data);
                    premiumQuery.execute();
                    String responseString = premiumQuery.get();
                    JSONObject responseJSON = new JSONObject(responseString);
                    if (ServerHandler.isErrored(responseString) == null) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Премиум куплен", Toast.LENGTH_SHORT).show();
                            }
                        });
                        startActivity(getIntent());
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Toast.makeText(context, "Ошибка покупки: " + responseJSON.getString("error_text"), Toast.LENGTH_SHORT).show();
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
}