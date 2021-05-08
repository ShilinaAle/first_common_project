package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.String.valueOf;

public class SignUpActivity extends AppCompatActivity {

    public static String server = "http://192.168.3.7/?action=singup";

    public EditText email;
    public EditText pass1;
    public EditText pass2;
    public EditText phone;

//    public String[] params = {};

    public static String email_in, pass1_in, pass2_in, tel_in, android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_sign_up);

        //сначала обратимся к нашим полям и кнопке
        Button btn = (Button) findViewById(R.id.signUpNow);
        email = (EditText) findViewById(R.id.email);
        pass1 = (EditText) findViewById(R.id.enterPassword);
        pass2 = (EditText) findViewById(R.id.checkPassword);
        // TODO: получить номер телефона без участия пользователя
        phone = (EditText) findViewById(R.id.phone_number);

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                email_in = email.getText().toString();
                pass1_in = pass1.getText().toString();
                pass2_in = pass2.getText().toString();
                tel_in = phone.getText().toString();
                android_id = android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                List<String> errors = new ArrayList<String>();

                if (!pass1_in.equals(pass2_in))
                    errors.add("Пароли не совпадают");
                if (!email_in.matches("\\w+@gmail\\.com"))
                    errors.add("Почта не принадлежит домену gmail.com");
                if (email_in.length() >= 50)
                    errors.add("Длина почты слишком большая");
                if (pass1_in.length() >= 50)
                    errors.add("Длина пароля слишком большая");

                // TODO: Выбрать формат номера. (Сейчас работаю с *+7или8**код**номер* без пробелов или иных разделителей)
                // preg_match("/([+]7|8)[0-9]{10}$/", $data['phone'], $matchesPhone);
                if (errors.size() > 0)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), errors.get(0), Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    HashMap<String, String> parames = new HashMap<>();
                    parames.put("phone", tel_in);
                    parames.put("email", email_in);
                    parames.put("pass", pass1_in);
                    parames.put("android_id", android_id);
//                String[] params = new String[] {email_in, pass1_in, pass2_in};
                    try
                    {
                        SendData SD = new SendData();
                        SD.parames = parames;
                        SD.server = server;
                        SD.action = "signup";
                        SD.contextt = getApplicationContext();
                        SD.execute();
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        });
    }

    private void EnterErrors(){
        //Is correct Edit texts?
    }

    public void onSingUpButtonClick(View view) {

        finish();
    }
}