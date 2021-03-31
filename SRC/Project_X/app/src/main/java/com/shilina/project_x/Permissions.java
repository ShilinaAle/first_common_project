package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class Permissions extends AppCompatActivity {

    @Override
    public void onBackPressed(){}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
    }

    public void ClickPerm(View view) {
        view.setClickable(false);
    }

    public void CheckPerm(View view){
        //Count permissions
        if (findViewById(R.id.switch1).isClickable() == true || findViewById(R.id.switch2).isClickable() == true ||
                findViewById(R.id.switch3).isClickable() == true ||findViewById(R.id.switch4).isClickable() == true ||
                findViewById(R.id.switch5).isClickable() == true ||findViewById(R.id.switch6).isClickable() == true ||
                findViewById(R.id.switch7).isClickable() == true )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Set title
            builder.setTitle("Не все разрешения выбраны");
            //Set message
            builder.setMessage("Необходимо предоставить все разрешения");
            //Positive yes button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Dismiss dialog
                    dialog.dismiss();
                }
            });
            //Show dialog
            builder.show();

        } else {
            //Open sign in
            Intent intentToLogin = new Intent(this, Login.class);
            startActivity(intentToLogin);
            finish();
        }
    }
}