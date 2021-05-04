package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class Later_callsActivity extends AppCompatActivity {
    //Initialize variable
    DrawerLayout drawerLayout;
    public static final String className = Thread.currentThread().getStackTrace()[2].getClassName();
    public static ArrayList<OneCall> plannedCallsList = new ArrayList<>();
    LinearLayout oneCallLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_later_calls);
        drawerLayout = findViewById(R.id.drawer_layout);
        //Assign variable
        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Отложенные звонки");

        plannedCallsList.add(new OneCall("89998194728-0", new Date()));
        plannedCallsList.add(new OneCall("89998194728-1", new Date()));
        plannedCallsList.add(new OneCall("89998194728-2", new Date()));
        plannedCallsList.add(new OneCall("89998194728-3", new Date()));
        plannedCallsList.add(new OneCall("89998194728-4", new Date()));
        plannedCallsList.add(new OneCall("89998194728-5", new Date()));
        plannedCallsList.add(new OneCall("89998194728-6", new Date()));
        plannedCallsList.add(new OneCall("89998194728-7", new Date()));
        plannedCallsList.add(new OneCall("89998194728-8", new Date()));
        plannedCallsList.add(new OneCall("89998194728-9", new Date()));

        Log.i("LOOK HERE: LCA", "PCList is: " + plannedCallsList.size());

        //TODO: Получение списка запланированных звонков от текущей даты и позже и запись их в calllist

        oneCallLayout = (LinearLayout) findViewById(R.id.scroll_view_layout);

        //Кнопка "Запланировать звонок"
        View planOneCall = getLayoutInflater().inflate(R.layout.one_call_view, oneCallLayout, false);
        planOneCall.setId(0);
        TextView plannedCallViewText = (TextView) planOneCall.findViewById(R.id.one_call_view_text);
        plannedCallViewText.setText("Чтобы запланировать звонок, нажмите:");
        Button plannedCallViewButton = (Button) planOneCall.findViewById(R.id.one_call_view_button);
        plannedCallViewButton.setText("Запланировать");
        plannedCallViewButton.setTextSize(COMPLEX_UNIT_SP, 8);
        plannedCallViewButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Окно со звонка с возможностью выбрать номер телефона
                //TODO: Добавление на сервер
                //TODO: Добавление в календарь
                String recipient = "89998194728-new";
                String message = "С вами запланирован звонок - new";
                //SMSHandler.sendSMS(getApplicationContext(), recipient, message);
            }
        });
        oneCallLayout.addView(planOneCall);
        Log.i("LOOK HERE: LCA", "poc number is: " + planOneCall.getId());

        //Вывод всех View в Layout'е
        /*
        for (int j = 0; j < oneCallLayout.getChildCount(); j++) {
            Log.i("LOOK HERE: LCA", "Child: " + oneCallLayout.getChildAt(j));
        }
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < plannedCallsList.size(); i++) {
            OneCall poc = plannedCallsList.get(i);
            View nextOneCall = getLayoutInflater().inflate(R.layout.one_call_view, oneCallLayout, false);
            nextOneCall.setId(i+1);
            TextView oneCallViewText = (TextView) nextOneCall.findViewById(R.id.one_call_view_text);
            oneCallViewText.setText("Звонок с абонентом: " + poc.caller + "\nБыл: " + poc.callStartTime + "\nБудет: " + poc.callPlannedTime);
            Button oneCallViewButton = (Button) nextOneCall.findViewById(R.id.one_call_view_button);
            oneCallViewButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View removedOneCall = (View) view.getParent();
                    Log.i("LOOK HERE: LCA", "Removing poc number: " + removedOneCall.getId());
                    //TODO: Всплывающее диалоговое окно
                    //TODO: Удаление с сервера
                    //TODO: Удаление из календаря
                    //TODO: Отправка сообщения пользователю?
                    oneCallLayout.removeView(removedOneCall);
                }
            });
            oneCallLayout.addView(nextOneCall);
            Log.i("LOOK HERE: LCA", "poc number is: " + nextOneCall.getId());
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
        XTools.closeDrawer(drawerLayout);
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