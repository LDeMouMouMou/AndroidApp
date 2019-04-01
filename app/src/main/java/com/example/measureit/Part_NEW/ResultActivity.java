package com.example.measureit.Part_NEW;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.measureit.MainActivity;
import com.example.measureit.Part_RECORD.RecordActivity;
import com.example.measureit.R;
import com.example.measureit.Part_NEW.ResultFragments.BiasFragment;
import com.example.measureit.Part_NEW.ResultFragments.DataFragment;
import com.example.measureit.Part_NEW.ResultFragments.OverviewFragment;
import com.example.measureit.Part_NEW.ResultFragments.PublishFragment;

public class ResultActivity extends AppCompatActivity {

    public BottomNavigationBar bottomNavigationBar;
    public TextView titleText;
    public Button homeButton;
    public Button backButton;
    // Defination of Fragments
    public OverviewFragment overviewFragment;
    public DataFragment dataFragment;
    public BiasFragment biasFragment;
    public PublishFragment publishFragment;
    public Fragment[] fragments;
    public int lastFragment;
    //
    private String dataSaverName;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        dataSaverName = getIntent().getStringExtra("dataSaverName");
        titleText = findViewById(R.id.headTitle);
        homeButton = findViewById(R.id.backHomepage);
        backButton = findViewById(R.id.backLastPage);
        backButton.setVisibility(View.INVISIBLE);
        overviewFragment = new OverviewFragment();
        dataFragment = new DataFragment();
        biasFragment = new BiasFragment();
        publishFragment = new PublishFragment();
        fragments = new Fragment[]{overviewFragment, dataFragment, biasFragment, publishFragment};
        // BottomNavigationBar Settings and Initialization
        bottomNavigationBar = findViewById(R.id.result_bottom_navigation);
        // Add new Navigation Elements
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.result_overview, "Overview"))
                .addItem(new BottomNavigationItem(R.drawable.result_data, "Data"))
                .addItem(new BottomNavigationItem(R.drawable.result_bias, "Bias"))
                .addItem(new BottomNavigationItem(R.drawable.result_publish, "Publish"));
        // Set NavigationBar Style
        bottomNavigationBar
                .setMode(BottomNavigationBar.MODE_SHIFTING)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
                .setBarBackgroundColor(R.color.colorBarColor)
                .setInActiveColor(R.color.colorDarkGray)
                .setActiveColor(R.color.colorWhite)
                .initialise();
        // Set the Default Fragment as Overview Fragment
        setDefaltFragment();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position){
                    case 0:
                        if (lastFragment != 0){
                            switchFragment(lastFragment, 0);
                            titleText.setText(R.string.NavigationName_Overview);
                            lastFragment = 0;
                        }
                        break;
                    case 1:
                        if (lastFragment != 1){
                            switchFragment(lastFragment, 1);
                            titleText.setText(R.string.NavigationName_Data);
                            lastFragment = 1;
                        }
                        break;
                    case 2:
                        if (lastFragment != 2){
                            switchFragment(lastFragment, 2);
                            titleText.setText(R.string.NavigationName_Bias);
                            lastFragment = 2;
                        }
                        break;
                    case 3:
                        if (lastFragment != 3){
                            switchFragment(lastFragment, 3);
                            titleText.setText(R.string.NavigationName_Publish);
                            lastFragment = 3;
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        if (getIntent().getBooleanExtra("isBackable", false)) {
            backButton.setVisibility(View.VISIBLE);
            homeButton.setVisibility(View.INVISIBLE);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ResultActivity.this, RecordActivity.class));
                }
            });
        }
    }

    private void setDefaltFragment(){
        getSupportFragmentManager()
                .beginTransaction().replace(R.id.result_mainview, overviewFragment)
                .show(overviewFragment)
                .commit();
        titleText.setText(R.string.NavigationName_Overview);
    }

    private void switchFragment(int lastFragment, int index){
        Bundle bundle = new Bundle();
        bundle.putString("dataSaverName", dataSaverName);
        fragments[index].setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(fragments[lastFragment]);
        if (!fragments[index].isAdded()){
            fragmentTransaction.add(R.id.result_mainview, fragments[index]);
        }
        fragmentTransaction.show(fragments[index]).commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_MUTE: return true;
            default: return false;
        }
    }

}
