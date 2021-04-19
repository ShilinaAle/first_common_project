package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Помощь");
    }

    public void ClickBack(View view){
        //Close window
        finish();
    }
}