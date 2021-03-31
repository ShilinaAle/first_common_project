package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

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
            findViewById(R.id.switch3).isClickable() == true || findViewById(R.id.switch4).isClickable() == true ||
            findViewById(R.id.switch5).isClickable() == true || findViewById(R.id.switch6).isClickable() == true ||
            findViewById(R.id.switch7).isClickable() == true)
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

            if (!isPermissionsGranted(all_permissions_str)) {
                givePermissions();
            }

            //Open sign in
            Intent intentToLogin = new Intent(this, Login.class);
            startActivity(intentToLogin);
            finish();
        }
    }

    //Работа с разрешениями
    ArrayList<String> needed_permissions_list = new ArrayList<>();
    String[] all_permissions_str = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_CALL_LOG,
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public boolean isPermissionsGranted(String[] all_permissions_str) {
        //Проверка разрешений
        needed_permissions_list = new ArrayList<> ();
        for (String permission : all_permissions_str) {
            int permission_status = ContextCompat.checkSelfPermission(this, permission); //Проверяем текущее разрешение
            if (permission_status != PackageManager.PERMISSION_GRANTED) { //Если разрешение не получено, =>
                Log.i("LOOK HERE: Permissions", permission+" is NOT");
                needed_permissions_list.add(permission); //то добавляем его в список неполученных
            } else {
                Log.i("LOOK HERE: Permissions", permission+" is GRANTED");
            }
        }
        if (needed_permissions_list.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void givePermissions() {
        if (!isPermissionsGranted(all_permissions_str)) {
            String[] needed_permission_str = needed_permissions_list.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, needed_permission_str, 1);
            for (String perm : needed_permission_str) {
                Log.i("LOOK HERE: Permissions", perm + " is NOW");
            }
        } else {
            Toast.makeText(this, "Разрешение уже предоставлено", Toast.LENGTH_SHORT).show();
        }
    }
    
    //TODO: Добавить 4 кнопки для запроса дополнительных разрешений

}