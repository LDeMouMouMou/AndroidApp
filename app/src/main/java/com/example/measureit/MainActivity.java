package com.example.measureit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.measureit.Part_NEW.ConfigurationActivity;
import com.example.measureit.Part_RECORD.RecordActivity;
import com.example.measureit.Part_SETTINGS.SettingsActivity;
import com.tencent.smtt.sdk.QbSdk;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        requestPermissions();
        Button newButton = findViewById(R.id.button_new);
        Button recordButton = findViewById(R.id.button_record);
        Button tutotialButton = findViewById(R.id.button_tutorial);
        Button settingsButton = findViewById(R.id.button_settings);
        Button infoButton = findViewById(R.id.button_information);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConfigurationActivity.class));
            }
        });
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecordActivity.class));
            }
        });
        tutotialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void requestPermissions() {
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE};
        if (!EasyPermissions.hasPermissions(MainActivity.this, perms)) {
            EasyPermissions.requestPermissions(MainActivity.this, "1", 10086, perms);
        }
    }


}
