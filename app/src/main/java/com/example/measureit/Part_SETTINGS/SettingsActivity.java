package com.example.measureit.Part_SETTINGS;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.measureit.R;

public class SettingsActivity extends AppCompatActivity {

    public TextView statueShow;
    public TextView rangeShow;
    public Button receiveStart;
    public Button receivePause;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        statueShow = findViewById(R.id.StatueShow);
        rangeShow = findViewById(R.id.RangeShow);
        receiveStart = findViewById(R.id.Receive_Start);
        receivePause = findViewById(R.id.Receive_Pause);

    }


}


