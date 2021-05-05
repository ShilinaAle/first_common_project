package com.shilina.project_x;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class SettingsActivity extends DrawerActivity {

    public static final String className = Thread.currentThread().getStackTrace()[2].getClassName();
    public static final String SP_FILE = "Accaunt";
    public static final String SP_THEME = "Theme";
    public static final String[] SP_THEMES = new String[] {"Shark", "Raccoon", "Panther"};
    public static final String SP_MODE_IN = "ModeIn";
    public static final String[] SP_MODES_IN = new String[] {"Hand", "Auto"};
    public static final String SP_MODE_OUT = "ModeOut";
    public static final String[] SP_MODES_OUT = new String[] {"Hand", "Off"};
    public static final String SP_USER = "User";
    public static final String SP_PREMIUM = "Premium"; //bool
    SharedPreferences sp_settings;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_settings);
        super.drawerLayout = findViewById(R.id.drawer_layout);
        super.className = className;
        setNickname();
        Log.i("LOOK HERE: SettingsActivity", "Opened activity is: " + className);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Настройки");
        sp_settings = getSharedPreferences(SP_FILE, MODE_PRIVATE);
        prefEditor = sp_settings.edit();

        //TODO: Получение данных с сервера

        //Обработка смены темы
        String sp_theme = sp_settings.getString(SP_THEME, SP_THEMES[0]);
        String curTheme = sp_theme;
        Spinner spinnerThemes = findViewById(R.id.spinnerTheme);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.themes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerThemes.setAdapter(adapter);
        spinnerThemes.setSelection(Arrays.asList(SP_THEMES).indexOf(sp_theme));
        spinnerThemes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                String newTheme = SP_THEMES[selectedItemPosition];
                Log.i("LOOK HERE: SettingsActivity", "Cur Theme is: " + curTheme + "\nNew Theme is: " + newTheme);
                if (!curTheme.equals(newTheme)) {
                    if (isPremium(getApplicationContext())) {
                        prefEditor.putString(SP_THEME, newTheme);
                        prefEditor.apply();
                        finish();
                        startActivity(getIntent());
                    } else {
                        spinnerThemes.setSelection(0);
                        Toast.makeText(getApplicationContext(), "Чтобы сменить тему, купите премиум", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Обработка смены режима вовремя мероприятий
        RadioGroup rgModeIn = findViewById(R.id.modeIn);
        Integer[] intModesIn = new Integer[2];
        for (int i=0; i<rgModeIn.getChildCount(); i++) {
            RadioButton rbModeIn = (RadioButton) rgModeIn.getChildAt(i);
            intModesIn[i] = rbModeIn.getId();
        }
        String sp_mode_in = sp_settings.getString(SP_MODE_IN, SP_MODES_IN[0]);
        String curModeIn= sp_mode_in;
        rgModeIn.check(intModesIn[Arrays.asList(SP_MODES_IN).indexOf(curModeIn)]);
        rgModeIn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int newModeInID) {
                String newModeIn = SP_MODES_IN[Arrays.asList(intModesIn).indexOf(newModeInID)];
                Log.i("LOOK HERE: SettingsActivity", "Cur ModeIn is: " + curModeIn + "\nNew ModeIn is: " + newModeIn);
                prefEditor.putString(SP_MODE_IN, newModeIn);
                prefEditor.apply();
            }
        });

        //Обработка смены режима вне мероприятий
        RadioGroup rgModeOut = findViewById(R.id.modeOut);
        Integer[] intModesOut = new Integer[2];
        for (int i=0; i<rgModeOut.getChildCount(); i++) {
            RadioButton rbModeOut = (RadioButton) rgModeOut.getChildAt(i);
            intModesOut[i] = rbModeOut.getId();
        }
        String sp_mode_out = sp_settings.getString(SP_MODE_OUT, SP_MODES_OUT[0]);
        String curModeOut= sp_mode_out;
        rgModeOut.check(intModesOut[Arrays.asList(SP_MODES_OUT).indexOf(curModeOut)]);
        rgModeOut.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int newModeOutID) {
                String newModeOut = SP_MODES_OUT[Arrays.asList(intModesOut).indexOf(newModeOutID)];
                Log.i("LOOK HERE: SettingsActivity", "Cur ModeOut is: " + curModeOut + "\nNew ModeOut is: " + newModeOut);
                prefEditor.putString(SP_MODE_OUT, newModeOut);
                prefEditor.apply();
            }
        });

    }

    public void checkButton(View view){

    }

    public void goPermissions(View view) {
        Intent intent = new Intent(getApplicationContext(), PermissionsActivity.class);
        intent.putExtra("calling-activity", className);
        startActivity(intent);
        finish();
    }

    public static void chooseTheme(Context context){
        SharedPreferences sp_settings = context.getSharedPreferences(SP_FILE, MODE_PRIVATE);
        String sp_theme = sp_settings.getString(SP_THEME, SP_THEMES[0]);
        switch (sp_theme) {
            case "Shark":
                context.setTheme(R.style.Theme_Project_X);
                break;
            case "Raccoon":
                context.setTheme(R.style.Theme_Project_X_enot);
                break;
            case "Panther":
                context.setTheme(R.style.Theme_Project_X_pantera);
                break;
        }
        Log.i("LOOK HERE: SettingsActivity", "Cur Theme is: " + sp_theme);
    }

    public static void setUser(Context context, String user) {
        SharedPreferences sp_settings = context.getSharedPreferences(SP_FILE, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sp_settings.edit();;
        prefEditor.putString(SP_USER, user);
        prefEditor.apply();
        Log.i("LOOK HERE: SettingsActivity", "Authorise as: " + user);
    }

    public static String getUser(Context context) {
        SharedPreferences sp_settings = context.getSharedPreferences(SP_FILE, MODE_PRIVATE);
        String sp_user = sp_settings.getString(SP_USER, null);
        Log.i("LOOK HERE: SettingsActivity", "Authorised as: " + sp_user);
        return sp_user;
    }

    public static boolean isAuthorised(Context context){
        if (getUser(context) == null){
            Log.i("LOOK HERE: SettingsActivity", "NOT Authorised");
            return false;
        } else {
            Log.i("LOOK HERE: SettingsActivity", "Authorised");
            return true;
        }
    }

    public static void setPremium(Context context, boolean value) {
        SharedPreferences sp_settings = context.getSharedPreferences(SP_FILE, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sp_settings.edit();;
        prefEditor.putBoolean(SP_PREMIUM, value);
        prefEditor.apply();
        Log.i("LOOK HERE: SettingsActivity", "Premium now: " + value);
    }

    public static boolean isPremium(Context context) {
        SharedPreferences sp_settings = context.getSharedPreferences(SP_FILE, MODE_PRIVATE);
        boolean sp_premium = sp_settings.getBoolean(SP_PREMIUM, false);
        Log.i("LOOK HERE: SettingsActivity", "Premium is: " + sp_premium);
        return sp_premium;
    }

}