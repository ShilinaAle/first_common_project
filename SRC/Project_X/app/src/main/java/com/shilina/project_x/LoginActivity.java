package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SettingsActivity.isAuthorised(this)) {
            Intent intent = new Intent(this, LaterCallsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            SettingsActivity.chooseTheme(this);
            setContentView(R.layout.activity_login);
        }
    }

    public void CheckLogin(View view) {
        String username = ((EditText) findViewById(R.id.login)).getText().toString();
        EditText et_password = (EditText) findViewById(R.id.password);
        et_password.clearFocus();
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //TODO: Local branch
        if ("admin".equals(username) && "admin".equals(et_password.getText().toString())) {
            SettingsActivity.setUser(this, username);
            SettingsActivity.setPremium(this, false);
            Toast.makeText(getApplicationContext(), "Вход выполнен!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LaterCallsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        //Обращение к серверу
        Runnable backgroundProcess = new Runnable() {
            public void run() {
                try {
                    HashMap<String, String> data = new HashMap<String, String>() {{
                        put("email", username);
                        put("pass", et_password.getText().toString());
                        put("android_id", android_id);
                    }};
                    ServerHandler loginQuery = new ServerHandler(ServerHandler.ACTION_LOGIN, data);
                    loginQuery.execute();
                    String responseString = loginQuery.get();
                    JSONObject responseJSON = new JSONObject(responseString);
                    Context context = getApplicationContext();
                    if (ServerHandler.isErrored(responseString) == null) {
                        SettingsActivity.setUser(context, data.get("email"));
                        SettingsActivity.setPremium(context, Boolean.parseBoolean(responseJSON.getString("user_premium")));
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Вход выполнен", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(context, LaterCallsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Toast.makeText(context, "Ошибка авторизации: " + responseJSON.getString("error_text"), Toast.LENGTH_SHORT).show();
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

    public void GoSignUp(View view) {
        Intent intentToSignUp = new Intent(this, SignUpActivity.class);
        startActivity(intentToSignUp);
    }

    @Override
    public void onBackPressed(){
        Log.i("LOOK HERE: DA", "App was closed");
        finishAffinity();
    }
}