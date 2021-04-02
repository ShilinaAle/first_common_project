package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Настройки");
    }

    public void ClickBack(View view){
        //Close window
        finish();
    }

    public void ClickBut1(View view) {
    }

    public void ClickBut2(View view) {
    }

    public void ClickBut3(View view) {
    }

    public void ClickBut4(View view) {
    }
}