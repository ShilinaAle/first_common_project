package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("О разработчиках");
    }

    public void ClickBack(View view){
        //Close window
        finish();
    }

}