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

        if (username.equals(user) && et_password.getText().toString().equals(hash)) {
            SettingsActivity.setUser(this, username);
            SettingsActivity.setPremium(this, status);
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            //TODO: Если этот пользователь заходил с этого утсройства, то загрузить настройки с сервера иначе:
            //SettingsActivity.setAllDefault(this, androidId);

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
    public void onBackPressed(){}
}