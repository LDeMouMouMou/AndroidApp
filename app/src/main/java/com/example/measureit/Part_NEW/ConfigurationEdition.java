package com.example.measureit.Part_NEW;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.measureit.MyClass.ConfigurationSaver;
import com.example.measureit.R;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class ConfigurationEdition extends AppCompatActivity {

    // Defination of All View Group
    public NiceSpinner niceSpinner;
    // Buttons
    public Button clearButton;
    public Button exitButton;
    public Button essentionQuestion;
    public Button RMDQuestion;
    // Checkboxes
    public CheckBox NonstdCheck; // Nonstandard Head
    public CheckBox EDCheck; // Ellipticity Detection
    public CheckBox RMDCheck; // Random Measuring Data
    public CheckBox SaveCheck; // Save Configuration
    // EditTexts
    public EditText ConcaveDeviation;
    public EditText ConvexDeviation;
    public EditText headInsideRadius; // Head Radius Inside
    public EditText curvedSurfaceHeight; // Curved Surface Height
    public EditText headTotalHeight;
    public EditText padHeight;
    // AlertDialog
    public AlertDialog saveDialog;
    public AlertDialog randomDialog;
    //
    public String name;
    public Boolean ifNew;
    public int typeChoose;
    public Boolean isPassed;
    public List<String> typeList = new ArrayList<>();
    //
    public ConfigurationSaver configurationSaver;
    //
    public Intent backIntent;




    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuratione_edtion);
        // Get Variable from the front activity
        name = getIntent().getStringExtra("configurationName");
        ifNew = getIntent().getBooleanExtra("ifNew", true);
        //
        configurationSaver = new ConfigurationSaver();
        backIntent = new Intent(ConfigurationEdition.this, ConfigurationActivity.class);
        // Get CheckBoxes
        NonstdCheck = findViewById(R.id.NonstdCheck);
        EDCheck = findViewById(R.id.EDCheck);
        RMDCheck = findViewById(R.id.RMDCheck);
        SaveCheck = findViewById(R.id.SaveCheck);
        // Get EditTexts
        ConcaveDeviation = findViewById(R.id.ConcaveDeviation);
        ConvexDeviation = findViewById(R.id.ConvexDeviation);
        headInsideRadius = findViewById(R.id.headInsideRadius);
        curvedSurfaceHeight = findViewById(R.id.curvedSurfaceHeight);
        headTotalHeight = findViewById(R.id.headTotalHeight);
        padHeight = findViewById(R.id.padHeight);
        // Get Buttons
        clearButton = findViewById(R.id.editionClear);
        exitButton = findViewById(R.id.editionExit);
        essentionQuestion = findViewById(R.id.editionQuestion1);
        RMDQuestion = findViewById(R.id.editionQuestion2);
        // Get Type Selection Spinner
        niceSpinner = findViewById(R.id.typeSpinner);
        typeList.add("Round");
        typeList.add("Ellipicity");
        typeList.add("Mixed");
        typeList.add("Unknown");
        // Set default data accrording to if newly generated
        // if not, acquire data from the sharedpreference
        // if so, set as default
        if (ifNew){
            configurationSaver.configurationSaverInit(getApplicationContext(), false, null);
            configurationSaver.addNewSaver(name);
            setDefaultBlank();
        }
        else{
            configurationSaver.configurationSaverInit(getApplicationContext(), true, name);
            setDefaultFromSaver();
        }
        //
        niceSpinner.attachDataSource(typeList);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ConfigurationEdition.this, typeList.get(position), Toast.LENGTH_SHORT).show();
                typeChoose = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //
        NonstdCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNonStdText(isChecked);
            }
        });
        //
        RMDCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (RMDCheck.isChecked()){
                    SaveCheck.setChecked(false);
                    SaveCheck.setClickable(false);
                    SaveCheck.setTextColor(Color.parseColor("#C2C2C2"));
                }
                else {
                    SaveCheck.setClickable(true);
                    SaveCheck.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        //
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultBlank();
            }
        });
        //
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paramsInspection()) {
                    updateParams();
                    startActivity(backIntent);
                    finish();
                }
                else {
                    Toast.makeText(ConfigurationEdition.this, "Error Saving! Please Check", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    // Disable all key except Multi-Task, for isolating the exit button
    // Home Button cannot be disabled
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                Toast.makeText(ConfigurationEdition.this, "Tap Exit to Back", Toast.LENGTH_SHORT)
                        .show();
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_MUTE: return true;
            default: return false;
        }
    }

    private void setDefaultFromSaver(){
        typeChoose = configurationSaver.getIntParams("typeNum");
        // Move the chosen type to the first position
        String typeChosen = typeList.get(typeChoose);
        String firstOne = typeList.get(0);
        if (!typeChosen.equals(firstOne)){
            typeList.set(0, typeChosen);
            typeList.set(typeChoose, firstOne);
        }
        // Set NonstdCheck and two edittexts based on if its checked state
        NonstdCheck.setChecked(configurationSaver.getBooleanParams("nonStdHead"));
        if (NonstdCheck.isChecked()){
            setNonStdText(true);
            ConcaveDeviation.setText(String.valueOf(configurationSaver.getFloatParams("concave")));
            ConvexDeviation.setText(String.valueOf(configurationSaver.getFloatParams("convex")));
        }
        else {
            setNonStdText(false);
        }
        // Set other views
        headInsideRadius.setText(String.valueOf(configurationSaver.getFloatParams("headRadius")));
        curvedSurfaceHeight.setText(String.valueOf(configurationSaver.getFloatParams("curvedSurface")));
        headTotalHeight.setText(String.valueOf(configurationSaver.getFloatParams("headTotal")));
        padHeight.setText(String.valueOf(configurationSaver.getFloatParams("padHeight")));
        EDCheck.setChecked(configurationSaver.getBooleanParams("ellipicityDetection"));
        RMDCheck.setChecked(configurationSaver.getBooleanParams("randomData"));
        SaveCheck.setChecked(configurationSaver.getBooleanParams("saveConfig"));
    }

    private void setDefaultBlank(){
        // NonStandard Head Default State: false
        NonstdCheck.setChecked(false);
        // ConcaveDeviation ConvexDeviation cannot be edited if the Nonstdcheck is false
        setNonStdText(NonstdCheck.isChecked());
        // Default EDCheck: true
        EDCheck.setChecked(true);
        // Default RMDCheck: false
        RMDCheck.setChecked(false);
        // Default saveCheck: true
        SaveCheck.setChecked(true);
        // Clear Contents
        headInsideRadius.setText("");
        curvedSurfaceHeight.setText("");
        headTotalHeight.setText("");
        padHeight.setText("");
    }

    private void updateParams(){
        configurationSaver.addParams(typeChoose, NonstdCheck.isChecked(),
                NonstdCheck.isChecked()?Float.parseFloat(ConcaveDeviation.getText().toString()):0,
                NonstdCheck.isChecked()?Float.parseFloat(ConvexDeviation.getText().toString()):0,
                Float.parseFloat(headInsideRadius.getText().toString()),
                Float.parseFloat(curvedSurfaceHeight.getText().toString()), Float.parseFloat(headTotalHeight.getText().toString()),
                EDCheck.isChecked(), Float.valueOf(String.valueOf(padHeight.getText())), RMDCheck.isChecked(),
                SaveCheck.isChecked());
    }

    private boolean paramsInspection(){
        isPassed = true;
        // When Non-Standard Head is specified, parameters must be filled
        if (NonstdCheck.isChecked()){
            if (isNullEmptyBlank(ConcaveDeviation.getText().toString()))
            {
                isPassed = false;
                ConcaveDeviation.setError("Needed!");
            }
            if (isNullEmptyBlank(ConvexDeviation.getText().toString()))
            {
                isPassed = false;
                ConvexDeviation.setError("Needed!");
            }
        }
        // Three Essential Parameters Check
        EditText[] editTexts = new EditText[]{headInsideRadius, curvedSurfaceHeight, headTotalHeight};
        for (int i = 0; i < editTexts.length; i++) {
            if (isNullEmptyBlank(editTexts[i].getText().toString())){
                isPassed = false;
                editTexts[i].setError("This Parameter cannot be Null!");
            }
            else if (!isDoubleOrFloat(editTexts[i].getText().toString()))
            {
                isPassed = false;
                editTexts[i].setError("Input Parameter is Invalid!");
            }
        }
        // Pad Height can be null
        if (!isNullEmptyBlank(padHeight.getText().toString())){
            if (!isDoubleOrFloat(padHeight.getText().toString())){
                isPassed = false;
                padHeight.setError("Input Parameter is Invalid!");
            }
        }
        else {
            isPassed = false;
            padHeight.setError("Please Specify the Pad Height");
        }
        // If Random Data is Enabled, ask for confirmation
        if (RMDCheck.isChecked()){
            final AlertDialog.Builder randomAlertBuilder = new AlertDialog.Builder(ConfigurationEdition.this)
                    .setCancelable(false)
                    .setTitle("Alert!")
                    .setMessage("Are you sure to use random data, all configuration will not be effected and saved!")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isPassed = false;
                            randomDialog.dismiss();
                            startActivity(backIntent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RMDCheck.setChecked(false);
                            randomDialog.dismiss();
                        }
                    });
            randomDialog = randomAlertBuilder.create();
            randomDialog.setCanceledOnTouchOutside(false);
            randomDialog.show();
        }
        // If SaveCheck is not clicked, ask
        else if (!SaveCheck.isChecked()) {
            final AlertDialog.Builder saveDialogBuilder = new AlertDialog.Builder(ConfigurationEdition.this)
                    .setTitle("Alert!")
                    .setMessage("Are you sure to abort this configuration?")
                    .setCancelable(false);
            saveDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isPassed = false;
                    saveDialog.dismiss();
                    startActivity(backIntent);
                    finish();
                }
            });
            saveDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SaveCheck.setChecked(true);
                    saveDialog.dismiss();
                }
            });
            saveDialog = saveDialogBuilder.create();
            saveDialog.setCanceledOnTouchOutside(false);
            saveDialog.show();
        }
        return isPassed;
    }

    private void setNonStdText(boolean isAvailable){
        if (isAvailable){
            ConcaveDeviation.setEnabled(true);
            ConcaveDeviation.setFocusable(true);
            ConcaveDeviation.setFocusableInTouchMode(true);
            ConcaveDeviation.setText("");
            ConvexDeviation.setEnabled(true);
            ConvexDeviation.setFocusable(true);
            ConvexDeviation.setFocusableInTouchMode(true);
            ConvexDeviation.setText("");
        }
        else {
            ConvexDeviation.setError(null, null);
            ConvexDeviation.setText(R.string.Unavailable);
            ConvexDeviation.setEnabled(false);
            ConvexDeviation.setFocusable(false);
            ConvexDeviation.setFocusableInTouchMode(false);
            ConcaveDeviation.setError(null, null);
            ConcaveDeviation.setText(R.string.Unavailable);
            ConcaveDeviation.setEnabled(false);
            ConcaveDeviation.setFocusable(false);
            ConcaveDeviation.setFocusableInTouchMode(false);
        }
    }

    private boolean isNullEmptyBlank(String str){
        if (str == null || "".equals(str) || "".equals(str.trim())){
            return true;
        }
        return false;
    }

    private boolean isDoubleOrFloat(String str) {
        Pattern pattern = compile("^[-]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
}
