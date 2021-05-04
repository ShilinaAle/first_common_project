package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_about_us);

        drawerLayout = findViewById(R.id.drawer_layout);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("О разработчиках");
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
        XTools.redirectActivity(this, Later_callsActivity.class);
        finish();
    }

    public void  ClickStatus(View view){
        XTools.redirectActivity(this, StatusActivity.class);
        finish();
    }

    public void  ClickSettings(View view){
        //Redirect activity
        XTools.redirectActivity(this, SettingsActivity.class);
        finish();
    }

    public void  ClickHelp(View view){
        XTools.redirectActivity(this, HelpActivity.class);
        finish();
    }

    public void ClickAboutUs(View view) {
        //Redirect activity

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickLogout(View view) {
        //Close app
        XTools.logout(this);
    }

    @Override
    public void onBackPressed(){}

}