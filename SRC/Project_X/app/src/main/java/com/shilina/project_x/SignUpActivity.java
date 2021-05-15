package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

public class SignUpActivity extends AppCompatActivity {

    EditText email;
    EditText pass1;
    EditText pass2;
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_sign_up);
        email = (EditText) findViewById(R.id.email);
        pass1 = (EditText) findViewById(R.id.enterPassword);
        pass2 = (EditText) findViewById(R.id.checkPassword);
        phone = (EditText) findViewById(R.id.phone_number);
    }

    private void EnterErrors() {
        //Is correct Edit texts?
    }

    public void onSingUpButtonClick(View view) {
        pass1.setBackgroundColor(Color.red(1));
        pass2.setBackgroundColor(Color.red(1));

        String email_in = email.getText().toString();
        String pass1_in = pass1.getText().toString();
        String pass2_in = pass2.getText().toString();
        String phone_in = phone.getText().toString();
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        List<String> errors = new ArrayList<String>();
        if (!email_in.matches("\\w+@gmail\\.com")) {
            email.setBackgroundColor(Color.red(1));
            errors.add("Почта не принадлежит домену gmail.com");
        }
        if (email_in.length() >= 50) {
            email.setBackgroundColor(Color.red(1));
            errors.add("Длина почты слишком большая");
        }
        if (!pass1_in.equals(pass2_in)) {
            pass1.setBackgroundColor(Color.red(1));
            pass2.setBackgroundColor(Color.red(1));
            errors.add("Пароли не совпадают");
        }
        if (pass1_in.length() >= 50) {
            pass1.setBackgroundColor(Color.red(1));
            pass2.setBackgroundColor(Color.red(1));
            errors.add("Длина пароля слишком большая");
        }
        if (!Pattern.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", (phone_in))) {
            phone.setBackgroundColor(Color.red(1));
            errors.add("Длина пароля слишком большая");
        }

        if (errors.size() > 0) {
            Toast.makeText(getApplicationContext(), errors.get(0), Toast.LENGTH_SHORT).show();
        } else {
            Runnable backgroundProcess = new Runnable() {
                public void run() {
                    try {
                        Context context = getApplicationContext();
                        HashMap<String, String> data = new HashMap<String, String>() {{
                            put("phone", phone_in);
                            put("email", email_in);
                            put("pass", pass1_in);
                            put("android_id", android_id);
                        }};
                        ServerHandler signupQuery = new ServerHandler(ServerHandler.ACTION_SIGNUP, data);
                        signupQuery.execute();
                        String responseString = signupQuery.get();
                        JSONObject responseJSON = new JSONObject(responseString);
                        if (ServerHandler.isErrored(responseString) == null) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                                }
                            });
                            SettingsActivity.setUser(context, data.get("email"));
                            SettingsActivity.setPremium(context, false);
                            Intent intent = new Intent(context, LaterCallsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        Toast.makeText(context, "Ошибка регистрации: " + responseJSON.getString("error_text"), Toast.LENGTH_SHORT).show();
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
}