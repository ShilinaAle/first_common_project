package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class Later_callsActivity extends AppCompatActivity {
    //Initialize variable
    DrawerLayout drawerLayout;
    public static final String className = Thread.currentThread().getStackTrace()[2].getClassName();
    public static ArrayList<OneCall> plannedCallsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_later_calls);
        drawerLayout = findViewById(R.id.drawer_layout);
        //Assign variable
        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Отложенные звонки");

        plannedCallsList.add(new OneCall("89998194728-0", new Date()));
        plannedCallsList.add(new OneCall("89998194728-1", new Date()));
        plannedCallsList.add(new OneCall("89998194728-2", new Date()));
        Log.i("LOOK HERE: LCA", "PCList is: " + plannedCallsList.size());

        //TODO: Получение списка запланированных звонков от текущей даты и запись их в calllist
        //TODO: Кнопка Запланировать звонок
        //TODO: layout для одного звонка: Номер и Дата звонка, кнопка Удалить, кнопка Позвонить сейчас

        LinearLayout oneCallLayout = (LinearLayout) findViewById(R.id.scroll_view_layout);
        for (int i = 0; i < plannedCallsList.size(); i++) {
            OneCall poc = plannedCallsList.get(i);
            View nextOneCall = getLayoutInflater().inflate(R.layout.one_call_view, oneCallLayout, false);
            nextOneCall.setId(i);
            TextView oneCallViewText = (TextView) nextOneCall.findViewById(R.id.one_call_view_text);
            oneCallViewText.setText("Звонок с абонентом: " + poc.caller + "\nБыл: " + poc.callStartTime + "\nБудет: " + poc.callPlannedTime);
            oneCallLayout.addView(nextOneCall);
            Log.i("LOOK HERE: LCA", "poc number is: " + nextOneCall.getId());
        }


/*
        TextView mMessageWindow = (TextView) findViewById(R.id.messageWindow);
        StringBuilder stringBuilder = new StringBuilder();

        String[] someMessege = new String[]{"some", "2some", "some", "2some", "some", "2some",
                "some", "2some", "some", "2some", "some", "2some", "some", "2some", "some", "2some",
                "some", "2some", "some", "2some", "some", "2some", "some", "2some"};

        for (int i = 0; i < someMessege.length; i++) {
            stringBuilder.append(someMessege[i]);
            stringBuilder.append("\n");
        }
        mMessageWindow.setText(stringBuilder.toString());
*/
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
        //if (XTools.getLogin() == "admin"){
        //    XTools.redirectActivity(this, Plan_callsActivity.class);
         //   finish();
        //} else
        XTools.redirectActivity(this, Plan_callsActivity.class);
        finish();
    }

    public void  ClickStatus(View view){

        XTools.redirectActivity(this, StatusActivity.class);
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

    @Override
    public void onBackPressed(){}

}