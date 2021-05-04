package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    //Initialize variable
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_help);
        //Assign variable
        drawerLayout = findViewById(R.id.drawer_layout);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Помощь");
    }

    public void ClickMenu(View view){
        //Open drawer
        XTools.openDrawer(drawerLayout);
    }

    public void  ClickLogo(View view){
        //Close drawer
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void ClickAboutUs(View view) {
        //Redirect activity
        XTools.redirectActivity(this, AboutUsActivity.class);
        finish();
    }

    public void ClickLogout(View view) {
        //Close app
        XTools.logout(this);
    }

    @Override
    public void onBackPressed(){}
}