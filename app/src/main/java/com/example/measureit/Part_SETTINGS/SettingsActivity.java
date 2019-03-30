package com.example.measureit.Part_SETTINGS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.measureit.MainActivity;
import com.example.measureit.R;

public class SettingsActivity extends AppCompatActivity {

    public SeekBar pointBar;
    public TextView pointText;
    public SeekBar angleBar;
    public TextView angleText;
    public Button backButton;
    //
    public SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        backButton = findViewById(R.id.backHomepage);
        pointBar = findViewById(R.id.pointNumberBar);
        pointText = findViewById(R.id.pointNumberBarText);
        angleBar = findViewById(R.id.angleNumberBar);
        angleText = findViewById(R.id.angleNumberBarText);
        settingsInit();
        pointBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                {
                    Toast.makeText(getApplicationContext(), "No less than 1, set as 1", Toast.LENGTH_SHORT)
                            .show();
                    pointBar.setProgress(1);
                    progress = 1;
                }
                pointText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        angleBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 10) {
                    angleBar.setProgress(10);
                    progress = 10;
                    Toast.makeText(getApplicationContext(), "Too small points leads more error, set as 10", Toast.LENGTH_SHORT)
                            .show();
                }
                angleText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsSave();
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    public void settingsInit() {
        sharedPreferences = getApplicationContext().getSharedPreferences("SystemSettings", MODE_PRIVATE);
        pointBar.setProgress(sharedPreferences.getInt("pointBarProgress", 10));
        pointText.setText(String.valueOf(sharedPreferences.getInt("pointBarProgress", 10)));
        angleBar.setProgress(sharedPreferences.getInt("angleBarProgress", 10));
        angleText.setText(String.valueOf(sharedPreferences.getInt("angleBarProgress", 10)));
    }

    public void settingsSave() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pointBarProgress", pointBar.getProgress());
        editor.putInt("angleBarProgress", angleBar.getProgress());
        editor.apply();
    }

}


