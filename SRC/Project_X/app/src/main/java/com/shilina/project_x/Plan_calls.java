package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Plan_calls extends AppCompatActivity {
    //Initialize variable
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_calls);

        //Assign variable
        drawerLayout = findViewById(R.id.drawer_layout);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Планирование звонков");
    }

    public void ClickMenu(View view){
        //Open drawer
        MainActivity.openDrawer(drawerLayout);
    }

    public void  ClickLogo(View view){
        //Close drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickLater(View view){
        //Redirect activity
        MainActivity.redirectActivity(this, Later_calls.class);
        finish();
    }

    public void  ClickPlan(View view){
        //Recreate activity
        recreate();

    }

    public void  ClickStatus(View view){
        //Redirect activity
        MainActivity.redirectActivity(this, Status.class);
        finish();
    }

    public void  ClickSettings(View view){
        //Redirect activity
        MainActivity.redirectActivity(this, Settings.class);
    }

    public void  ClickHelp(View view){
        //Redirect activity
        MainActivity.redirectActivity(this, Help.class);
    }

    public void ClickAboutUs(View view) {
        //Redirect activity
        MainActivity.redirectActivity(this, AboutUs.class);
    }

    public void ClickLogout(View view) {
        //Close app
        MainActivity.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Close drawer
        MainActivity.closeDrawer(drawerLayout);
    }
}