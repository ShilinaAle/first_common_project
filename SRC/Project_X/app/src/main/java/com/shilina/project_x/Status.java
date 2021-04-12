package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Status extends AppCompatActivity {
    //Initialize variable
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        //Assign variable
        drawerLayout = findViewById(R.id.drawer_layout);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Мой статус");
    }

    public void ClickMenu(View view){
        //Open drawer
        XTools.openDrawer(drawerLayout);
    }

    public void  ClickLogo(View view){
        //Close drawer
        XTools.closeDrawer(drawerLayout);
    }

    public void ClickLater(View view){
        //Redirect activity
        XTools.redirectActivity(this, Later_calls.class);
        finish();
    }

    public void  ClickPlan(View view){
        //Redirect activity
        XTools.redirectActivity(this, Plan_calls.class);
        finish();
    }

    public void  ClickStatus(View view){
        //Recreate activity
        recreate();
    }

    public void  ClickSettings(View view){
        //Redirect activity
        XTools.redirectActivity(this, Settings.class);
    }

    public void  ClickHelp(View view){
        //Redirect activity
        XTools.redirectActivity(this, Help.class);
    }

    public void ClickAboutUs(View view) {
        //Redirect activity
        XTools.redirectActivity(this, AboutUs.class);
    }

    public void ClickLogout(View view) {
        //Close app
        XTools.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Close drawer
        XTools.closeDrawer(drawerLayout);
    }
}