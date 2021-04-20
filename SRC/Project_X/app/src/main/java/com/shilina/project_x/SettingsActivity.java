package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
//implements AdapterView.OnItemSelectedListener
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView textById = findViewById(R.id.nameOfWindow);
        textById.setText("Настройки");
        Spinner spinnerThemes = findViewById(R.id.spinnerTheme);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.themes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerThemes.setAdapter(adapter);


        RadioGroup radioGroup = findViewById(R.id.radioTime);
        //RadioButton
    }
    public void checkButton(View view){

    }

    public void ClickBack(View view){
        //Close window
        finish();
    }


    public void goPermissions(View view) {
        XTools.redirectActivity(this, PermissionsActivity.class);
        finish();
    }
}