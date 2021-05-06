package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    public static String server = "http://192.168.3.7/?action=singup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_sign_up);
    }

    public void onSingUpButtonClick(View view) {
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String phone = ((EditText) findViewById(R.id.phone_number)).getText().toString();
        String pass1 = ((EditText) findViewById(R.id.enterPassword)).getText().toString();
        String pass2 = ((EditText) findViewById(R.id.checkPassword)).getText().toString();

        boolean isPhoneCorrect = Pattern.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", phone);
        Log.i("LOOK HERE: SUA", "Phone is correct: " + isPhoneCorrect);
        boolean isEmailCorrect = Pattern.matches("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}", email);
        Log.i("LOOK HERE: SUA", "Email is correct: " + isEmailCorrect);
        boolean isPasswordCorrect = false;
        if (pass1 != null) {
            isPasswordCorrect = pass1.equals(pass2);
        }
        Log.i("LOOK HERE: SUA", "Password is correct: " + isPasswordCorrect);
        if (isPhoneCorrect && isEmailCorrect && isPasswordCorrect) {
            HashMap<String, String> parames = new HashMap<>();
            parames.put("email", email);
            parames.put("phone", pass1);
            parames.put("pass1", pass2);
//                String[] params = new String[] {email_in, pass1_in, pass2_in};
            try {
                SendData SD = new SendData();
                SD.parames = parames;
                SD.server = server;
                SD.contextt = getApplicationContext();
                SD.execute();
            } catch (Exception e) {

            }

            finish();
            Log.i("LOOK HERE: SUA", "Registration success");
        } else {
            Toast.makeText(getApplicationContext(), "Введите корректные данные", Toast.LENGTH_SHORT).show();
            Log.i("LOOK HERE: SUA", "Registration denied");
        }
    }
}