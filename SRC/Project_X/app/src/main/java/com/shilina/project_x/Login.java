package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    EditText username;
    EditText password;

    @Override
    public void onBackPressed(){}

    //Get from database
    private String user = "admin";
    private String hash = "admin";
    private boolean status = true;


    public void CheckLogin(View view) {
        // Если введенные логин и пароль будут словом "admin",
        // показываем Toast сообщение об успешном входе:
        username = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        password.clearFocus();
        if (username.getText().toString().equals(user) &&
                password.getText().toString().equals(hash)) {
            XTools.setLogin(user);
            XTools.setPremium(status);
            Toast.makeText(getApplicationContext(), "Вход выполнен!", Toast.LENGTH_SHORT).show();
            XTools.redirectActivity(this, Later_calls.class);
            XTools.isAuthorized = true; //TODO: указать когда он становится false
            finish();
        }
    }

    public void GoSignUp(View view) {
        Intent intentToSignUp = new Intent(this, SignUp.class);
        startActivity(intentToSignUp);
    }
}