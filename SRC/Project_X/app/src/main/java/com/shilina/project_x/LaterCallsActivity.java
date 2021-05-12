package com.shilina.project_x;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class LaterCallsActivity extends DrawerActivity {

    public static final String className = Thread.currentThread().getStackTrace()[2].getClassName();
    public static ArrayList<OneCall> plannedCallsList = new ArrayList<>();
    LinearLayout oneCallLayout;
    public static PlanCallLayout planLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_later_calls);
        super.drawerLayout = findViewById(R.id.drawer_layout);
        super.className = className;
        setNickname();
        Log.i("LOOK HERE: LCA", "Opened activity is: " + className);

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
                if (planLayout == null) {
                    planLayout = new PlanCallLayout(getApplicationContext(),"", Calendar.getInstance().getTime());
                }
                if (!planLayout.isShown) {
                    planLayout.addBubble();
                    //TODO: Назначить закрытие окна кнопкой назад
                }

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

    @Override
    protected void onPause() {
        super.onPause();
        closePCL();
    }
    
    @Override
    public void openDrawer(DrawerLayout drawerLayout) {
        super.openDrawer(drawerLayout);
        closePCL();
    }

    static void closePCL(){
        if (planLayout != null) {
            planLayout.removePCL();
            planLayout = null;
        }
    }
}