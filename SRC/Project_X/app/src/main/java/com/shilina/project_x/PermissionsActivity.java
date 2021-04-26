package com.shilina.project_x;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class PermissionsActivity extends AppCompatActivity {

    Switch permSwitcher;
    public String callingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callingActivity = getIntent().getStringExtra("calling-activity");
        Log.i("LOOK HERE: PermissionsActivity", "CALLING ACT IS: " + callingActivity);

        if (callingActivity == null && isPermissionsGranted(all_permissions_str) && android.provider.Settings.canDrawOverlays(this)) {
            onContinueClick(new View(getApplicationContext()));
        } else {
            Log.i("LOOK HERE: PermissionsActivity", "Settings className IS: " + SettingsActivity.className);
            Log.i("LOOK HERE: PermissionsActivity", "Already Authorized: " + XTools.isAuthorized);
            setContentView(R.layout.activity_permissions);
            permSwitcher = findViewById(R.id.switch1);
            //Метод обработки смены значения кнопки
            permSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (permSwitcher.isChecked()) {
                        if (!isPermissionsGranted(all_permissions_str)) {
                            givePermissions();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (callingActivity != null) {
            if (callingActivity.equals(SettingsActivity.className)) {
                permSwitcher.setChecked(false);
                if (!isPermissionsGranted(all_permissions_str)) {
                    permSwitcher.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Еще не ВСЕ разрешения приняты", Toast.LENGTH_SHORT).show();
                } else {
                    permSwitcher.setChecked(true);
                }
            }
        }
    }

    //Метод, блокирующий выключение кнопки
    public void onPermClick(View view) {
        if (!permSwitcher.isChecked()) {
            Toast.makeText(getApplicationContext(), "Отозвать разрешения можно только в настройках устройства", Toast.LENGTH_SHORT).show();
            permSwitcher.setChecked(true);
        }
    }

    public void onContinueClick(View view) {
        if (isPermissionsGranted(all_permissions_str) && android.provider.Settings.canDrawOverlays(this)) {
            Intent service = new Intent(this, BroadcastService.class);
            this.startService(service);
            //Open sign in
            if (XTools.isAuthorized) {
                XTools.redirectActivity(this, Later_callsActivity.class);
            } else {
                XTools.redirectActivity(this, LoginActivity.class);
            }
            Log.i("LOOK HERE: PermissionsActivity", "Already Authorized: " + XTools.isAuthorized);
            Toast.makeText(getApplicationContext(), "Разрешения находятся в разделе \"Найстроки\"", Toast.LENGTH_SHORT).show();
        }
    }

    //Списки с разрешениями
    ArrayList<String> needed_permissions_list = new ArrayList<>();
    String[] all_permissions_str = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_CALL_LOG,
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    //Метод, проверяющий базовые разрешения
    public boolean isPermissionsGranted(String[] all_permissions_str) {
        needed_permissions_list = new ArrayList<> ();
        for (String permission : all_permissions_str) {
            int permission_status = ContextCompat.checkSelfPermission(this, permission); //Проверяем текущее разрешение
            if (permission_status != PackageManager.PERMISSION_GRANTED) { //Если разрешение не получено, =>
                Log.i("LOOK HERE: PermissionsActivity", permission+" is NOT");
                needed_permissions_list.add(permission); //то добавляем его в список неполученных
            } else {
                Log.i("LOOK HERE: PermissionsActivity", permission+" is GRANTED");
            }
        }
        if (needed_permissions_list.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    //Метод, запрашивающий базовые разрешения
    public void givePermissions() {
        String[] needed_permission_str = needed_permissions_list.toArray(new String[0]);
        ActivityCompat.requestPermissions(this, needed_permission_str, 1);
        for (String perm : needed_permission_str) {
            Log.i("LOOK HERE: PermissionsActivity", perm + " is NOW");
        }
    }

    //Метод, запрашивающий разрешение на показ поверх остальных приложений
    public void onOverClick(View view) {
        if (!android.provider.Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            Log.i("LOOK HERE: PermissionsActivity", "Overlay is NOW");
        } else {
            Toast.makeText(this, "Разрешение уже предоставлено", Toast.LENGTH_SHORT).show();
        }
    }

    //Метод, запрашивающий разрещение на работу в фоновым режиме
    public void onBackClick(View view) {
        PowerManager powerMan = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!powerMan.isIgnoringBatteryOptimizations(getPackageName())) {
            Log.i("LOOK HERE: PermissionsActivity", "Battery Saver is " + powerMan.isIgnoringBatteryOptimizations(getPackageName()));
            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "На вашем телефоне это не нужно", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Разрешение уже предоставлено", Toast.LENGTH_SHORT).show();
        }
    }

    //Метод, запрашивающий отключение оптимизации энергопотребления
    public void onBatClick(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"));
        intent.putExtra("package_name", getPackageName());
        intent.putExtra("package_label", getText(R.string.app_name));
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            startActivity(intent);
            Toast.makeText(this, "Пожалуйста, выберите \"Нет ограничений\"", Toast.LENGTH_LONG).show();
        } else {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                startActivity(intent);
                Toast.makeText(this, "Пожалуйста,\nотключите оптимизацию энергопотребления\nдля приложения Project_X", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "На вашем телефоне это не нужно", Toast.LENGTH_SHORT).show();
            }
        }
        Log.i("LOOK HERE: PermissionsActivity", "Battery Saver is NOW");
    }

    //Метод, запрашивающий отключение оптимизации энергопотребления
    public void onAutoClick(View view) {
        Intent[] POWERMANAGER_INTENTS = {
                new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
                new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
                new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
                new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
                new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
                new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
                new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
                new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
                new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))};

        boolean powers = true;
        for (Intent intent : POWERMANAGER_INTENTS) {
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                startActivity(intent);
                Toast.makeText(this, "Пожалуйста, включите Автозапуск для приложения Project_X", Toast.LENGTH_LONG).show();
                powers = false;
                break;
            }
        }
        if (powers) {
            Toast.makeText(this, "На вашем телефоне это не нужно", Toast.LENGTH_SHORT).show();
        }
    }
}