package com.shilina.project_x;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class DrawerActivity extends AppCompatActivity {

    String className;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Открыть новое окно
    public void redirectActivity(Activity activity, Class aclass){
        Intent intent = new Intent(activity, aclass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        finish();
    }

    //Открыть боковое меню
    public void openDrawer(DrawerLayout drawerLayout) {
        if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
            Log.i("LOOK HERE: DA", "Side menu was OPENED");
        }
    }
    //Закрыть боковое меню
    public void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            Log.i("LOOK HERE: DA", "Side menu was CLOSED");
        }
    }

    public void onMenuClick(View view){
        openDrawer(drawerLayout);
    }

    public void onLogoClick(View view){
        closeDrawer(drawerLayout);
    }

    public void onLaterActClick(View view){
        if (className.equals(LaterCallsActivity.className)) {
            closeDrawer(drawerLayout);
            Log.i("LOOK HERE: DA", "Activity already opened");
        } else {
            redirectActivity(this, LaterCallsActivity.class);
            Log.i("LOOK HERE: DA", "Redirecting to LaterCalls");
        }
    }

    public void onStatusActClick(View view){
        if (className.equals(StatusActivity.className)) {
            closeDrawer(drawerLayout);
            Log.i("LOOK HERE: DA", "Activity already opened");
        } else {
            redirectActivity(this, StatusActivity.class);
            Log.i("LOOK HERE: DA", "Redirecting to Status");
        }
    }

    public void onSettingsActClick(View view){
        if (className.equals(SettingsActivity.className)) {
            closeDrawer(drawerLayout);
            Log.i("LOOK HERE: DA", "Activity already opened");
        } else {
            redirectActivity(this, SettingsActivity.class);
            Log.i("LOOK HERE: DA", "Redirecting to Settings");
        }
    }

    public void onHelpActClick(View view){
        if (className.equals(HelpActivity.className)) {
            closeDrawer(drawerLayout);
            Log.i("LOOK HERE: DA", "Activity already opened");
        } else {
            redirectActivity(this, HelpActivity.class);
            Log.i("LOOK HERE: DA", "Redirecting to Help");
        }
    }

    public void onAboutUsActClick(View view) {
        if (className.equals(AboutUsActivity.className)) {
            closeDrawer(drawerLayout);
            Log.i("LOOK HERE: DA", "Activity already opened");
        } else {
            redirectActivity(this, AboutUsActivity.class);
            Log.i("LOOK HERE: DA", "Redirecting to AboutUs");
        }
    }

    //TODO:Выйти из приложения
    public void ClickLogout(View view) {
        //Close app
        XTools.logout(this);
    }
}
