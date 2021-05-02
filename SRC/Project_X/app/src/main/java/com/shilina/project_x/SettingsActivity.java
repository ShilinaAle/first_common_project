package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {
//implements AdapterView.OnItemSelectedListener

    public static final String className = Thread.currentThread().getStackTrace()[2].getClassName();
    public static final String SP_FILE = "Accaunt";
    public static final String SP_NAME = "Name";
    public static final String SP_THEME = "Theme";
    public static final String[] SP_THEMES = new String[] {"Shark", "Raccoon", "Panther"};
    public static final String SP_MODE_IN = "ModeIn";
    public static final String SP_MODE_OUT = "ModeOut";
    SharedPreferences sp_settings;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.chooseTheme(this);
        setContentView(R.layout.activity_settings);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Настройки");
        SharedPreferences settings = getSharedPreferences(SP_FILE, MODE_PRIVATE);
        String sp_theme = settings.getString(SP_THEME, SP_THEMES[0]);
        String curTheme = sp_theme;

        prefEditor = settings.edit();
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
                    prefEditor.putString(SP_THEME, newTheme);
                    prefEditor.apply();
                    finish();
                    startActivity(getIntent());
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        RadioGroup modeIn = findViewById(R.id.modeIn);
        //RadioButton
    }

    public void checkButton(View view){

    }

    public void ClickBack(View view){
        //Close window
        finish();
    }


    public void goPermissions(View view) {
        Intent intent = new Intent(getApplicationContext(), PermissionsActivity.class);
        intent.putExtra("calling-activity", className);
        startActivity(intent);
        finish();
    }


    public static void chooseTheme(Context context){
        SharedPreferences settings = context.getSharedPreferences(SP_FILE, MODE_PRIVATE);
        String sp_theme = settings.getString(SP_THEME, SP_THEMES[0]);
        switch (sp_theme) {
            case "Shark":
                context.setTheme(R.style.Theme_Project_X);
                break;
            case "Raccoon":
                context.setTheme(R.style.Theme_Project_X_enot);
                break;
            case "Panther":
                break;
        }
        Log.i("LOOK HERE: SettingsActivity", "Cur Theme is: " + sp_theme);
    }

}