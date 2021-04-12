package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Later_calls extends AppCompatActivity {
    //Initialize variable
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (XTools.isFirstLaunch) {
            //Open permissions activity
            XTools.redirectActivity(this, Permissions.class);
        } else if (!XTools.isAuthorized) {
            XTools.redirectActivity(this, Login.class);
        } else {
            setContentView(R.layout.activity_later_calls);
            //Assign variable
            drawerLayout = findViewById(R.id.drawer_layout);

            TextView textById = findViewById(R.id.nameOfWindow);
            textById.setText("Отложенные звонки");
        }


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
        //Recreate activity
        recreate();
    }

    public void  ClickPlan(View view){
        //Redirect activity to about us
        XTools.redirectActivity(this, Plan_calls.class);
        finish();
    }

    public void  ClickStatus(View view){
        //Redirect activity to about us
        XTools.redirectActivity(this, Status.class);
        finish();
    }

    public void  ClickSettings(View view){
        //Redirect activity to about us
        XTools.redirectActivity(this, Settings.class);
    }

    public void  ClickHelp(View view){
        //Redirect activity to about us
        XTools.redirectActivity(this, Help.class);
    }

    public void ClickAboutUs(View view) {
        //Redirect activity to about us
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