package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AboutUsActivity extends DrawerActivity {

    public static final String className = Thread.currentThread().getStackTrace()[2].getClassName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_about_us);
        super.drawerLayout = findViewById(R.id.drawer_layout);
        super.className = className;
        setNickname();
        Log.i("LOOK HERE: AUA", "Opened activity is: " + className);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("О разработчиках");
    }
}