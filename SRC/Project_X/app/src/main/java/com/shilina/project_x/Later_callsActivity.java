package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Later_callsActivity extends AppCompatActivity {
    //Initialize variable
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (XTools.isFirstLaunch) {
            //Open permissions activity
            XTools.redirectActivity(this, PermissionsActivity.class);
        } else if (!XTools.isAuthorized) {
            XTools.redirectActivity(this, LoginActivity.class);
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
        XTools.redirectActivity(this, Plan_callsActivity.class);
        finish();
    }

    public void  ClickStatus(View view){
        //Redirect activity to about us
        XTools.redirectActivity(this, Status.class);
        finish();
    }

    public void  ClickSettings(View view){
        //Redirect activity to about us
        XTools.redirectActivity(this, SettingsActivity.class);
    }

    public void  ClickHelp(View view){
        //Redirect activity to about us
        XTools.redirectActivity(this, HelpActivity.class);
    }

    public void ClickAboutUs(View view) {
        //Redirect activity to about us
        XTools.redirectActivity(this, AboutUsActivity.class);
    }

    public void ClickLogout(View view) {
        //Close app
        XTools.logout(this);
    }

}