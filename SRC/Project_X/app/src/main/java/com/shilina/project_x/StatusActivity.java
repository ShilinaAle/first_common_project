package com.shilina.project_x;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;



public class StatusActivity extends DrawerActivity{

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText pay_number, pay_month, pay_year, pay_name, pay_cvc;
    private Button pay_cancel, pay_buy;
    private TextView textAlert;

    public void  createPayMenuDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View pay_view = getLayoutInflater().inflate(R.layout.activity_pay, null);
        pay_number = (EditText) pay_view.findViewById(R.id.pay_number);
        pay_month = (EditText) pay_view.findViewById(R.id.pay_month);
        pay_year = (EditText) pay_view.findViewById(R.id.pay_year);
        pay_name = (EditText) pay_view.findViewById(R.id.pay_name);
        pay_cvc = (EditText) pay_view.findViewById(R.id.pay_cvc);

        pay_buy = (Button) pay_view.findViewById(R.id.buy);
        pay_cancel = (Button) pay_view.findViewById(R.id.pay_cancel);

        textAlert = (TextView) pay_view.findViewById(R.id.textAlert);

        dialogBuilder.setView(pay_view);
        dialog = dialogBuilder.create();
        dialog.show();


        pay_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define cancel button here!
                dialog.dismiss();
            }
        });

        pay_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pay_number.getText().toString().length() < 16 ||
                        Integer.parseInt(pay_month.getText().toString()) < 1 || Integer.parseInt(pay_month.getText().toString()) > 12 ||
                        pay_cvc.getText().toString().length() < 3 || Integer.parseInt(pay_year.getText().toString()) < 21 ||
                        pay_name.getText().toString() == "") {
                    textAlert.setVisibility(View.VISIBLE);
                    return;
                } else {

                    Log.i("LOOK HERE: SA", "Purchased premium ");
                    SettingsActivity.setPremium(getApplicationContext(), true);

                    Runnable backgroundProcess = new Runnable() {
                        public void run() {
                            try {
                                Context context = getApplicationContext();
                                HashMap<String, String> data = new HashMap<String, String>() {{
                                    put("email", SettingsActivity.getUser(context));
                                    put("summ", "250");
                                    put("pay_date", CalendarHandler.getTimeStringFromDate(Calendar.getInstance().getTime(), "dd.MM.yy"));
                                    put("pay_time", CalendarHandler.getTimeStringFromDate(Calendar.getInstance().getTime(), "HH:mm"));
                                }};
                                ServerHandler premiumQuery = new ServerHandler(ServerHandler.ACTION_SET_PREMIUM, data);
                                premiumQuery.execute();
                                String responseString = premiumQuery.get();
                                JSONObject responseJSON = new JSONObject(responseString);
                                if (ServerHandler.isErrored(responseString) == null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(context, "Премиум куплен", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    startActivity(getIntent());
                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
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
                    Thread thread = new Thread(null, backgroundProcess, "Background");
                    thread.start();
                }
            }
        });

    }


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
        createPayMenuDialog();
    }

    @Override
    public void onPause() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onPause();
    }
}