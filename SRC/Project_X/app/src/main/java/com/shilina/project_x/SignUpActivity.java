package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    public static String server = "http://192.168.3.7/?action=singup";

    public EditText email;
    public EditText pass1;
    public EditText pass2;
    public static String phone = "";

//    public String[] params = {};

    public static String email_in = "";
    public static String pass1_in = "";
    public static String pass2_in = "";
    public static String tel_in = "";

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
        // TODO: получить номер телефона
        phone = "89851234567";

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                email_in = email.getText().toString();
                pass1_in = pass1.getText().toString();
                pass2_in = pass2.getText().toString();
                tel_in = phone;

                // TODO: Сделать проверку перед отправкой, что все поля заполнены, причём корректно
                // TODO: Выбрать формат номера. (Сейчас работаю с *+7или8**код**номер* без пробелов или иных разделителей)
                // preg_match("/([+]7|8)[0-9]{10}$/", $data['phone'], $matchesPhone);
                // проверка, что пароли совпадают
                // TODO: прописать ограничения на кол-во знаков (взять из БД)

                HashMap<String, String> parames = new HashMap<>();
                parames.put("phone", tel_in);
                parames.put("email", email_in);
                parames.put("pass1", pass1_in);
//                String[] params = new String[] {email_in, pass1_in, pass2_in};
                try
                {
                    SendData SD = new SendData();
                    SD.parames = parames;
                    SD.server = server;
                    SD.contextt = getApplicationContext();
                    SD.execute();
                }
                catch (Exception e)
                {

                }
            }
        });
    }

    private void EnterErrors(){
        //Is correct Edit texts?
    }

    public void onSingUpButtonClick(View view) {
        //Connect with db

        finish();
    }
}