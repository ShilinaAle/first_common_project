package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import static java.lang.String.valueOf;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SettingsActivity.isAuthorised(this)) {
            Intent intent = new Intent(this, LaterCallsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            SettingsActivity.chooseTheme(this);
            setContentView(R.layout.activity_login);
        }
    }

    public void CheckLogin(View view) {
        //TODO: Сравнить с записями с базой данных

        String user = "admin";
        String hash = "admin";
        String username = ((EditText) findViewById(R.id.login)).getText().toString();
        EditText et_password = (EditText) findViewById(R.id.password);
        et_password.clearFocus();
        boolean status = false;
        // TODO: получить Android ID (обязательно должен быть string)
        String android_id = "402";//valueOf((int) (Math.random() * 1000));


        // Get from database:
        HashMap<String, String> parames = new HashMap<>();
        parames.put("email", username);
        parames.put("pass", et_password.getText().toString());
        parames.put("android_id", android_id);
        try
        {
            SendData SD = new SendData();
            SD.parames = parames;
            SD.server = "http://192.168.3.7/?action=login";
            SD.action = "login";
            SD.contextt = getApplicationContext();
            SD.execute();
        }
        catch (Exception e)
        {

        }

        // НУЖНО БУДЕТ УБРАТЬ ЭТОТ IF, ПОТОМУ ЧТО ОН ТЕПЕРЬ НАХОДИТСЯ В SendData И ЕСЛИ ОБРАБОТКА ЛОГИНА ИДЁТ ЧЕРЕЗ СЕРВЕР, ТО ЕГО ЗДЕС БЫТЬ НЕ ДОЛЖНО
        // НО Т.К. СЕЙЧАС СЕРВЕР ТОЛЬКО У МЕНЯ, ТО ПОКА НЕ УБИРАЮ ОКОНЧАТЕЛЬНО.
        if (username.equals(user) && et_password.getText().toString().equals(hash)) {
            SettingsActivity.setUser(this, username);
            SettingsActivity.setPremium(this, status);
            Toast.makeText(getApplicationContext(), "Вход выполнен!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LaterCallsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
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