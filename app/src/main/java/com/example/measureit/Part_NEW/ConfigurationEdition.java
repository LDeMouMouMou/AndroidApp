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
import android.widget.SeekBar;
import android.widget.TextView;
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
    public Button RMDQuestion;
    // Checkboxes
    public CheckBox NonstdCheck; // Nonstandard Head
    public CheckBox EDCheck; // Ellipticity Detection
    public CheckBox RMDCheck; // Random Measuring Data
    // EditTexts
    public EditText ConcaveDeviation;
    public EditText ConvexDeviation;
    public EditText headInsideRadius; // Head Radius Inside
    public EditText curvedSurfaceHeight; // Curved Surface Height
    public EditText headTotalHeight;
    public EditText padHeight;
    // SeekBar and Textviews
    public SeekBar pointBar;
    public TextView pointText;
    public SeekBar angleBar;
    public TextView angleText;
    // AlertDialog
    public AlertDialog randomDialog;
    //
    public String name;
    public Boolean ifNew;
    public int typeChoose;
    public Boolean isPassed;
    public List<String> typeList = new ArrayList<>();
    //
    public ConfigurationSaver configurationSaver;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuratione_edtion);
        // Get Variable from the front activity
        name = getIntent().getStringExtra("configurationName");
        ifNew = getIntent().getBooleanExtra("ifNew", true);
        //
        configurationSaver = new ConfigurationSaver();
        // Get CheckBoxes
        NonstdCheck = findViewById(R.id.NonstdCheck);
        EDCheck = findViewById(R.id.EDCheck);
        RMDCheck = findViewById(R.id.RMDCheck);
        // Get EditTexts
        ConcaveDeviation = findViewById(R.id.ConcaveDeviation);
        ConvexDeviation = findViewById(R.id.ConvexDeviation);
        headInsideRadius = findViewById(R.id.headInsideRadius);
        curvedSurfaceHeight = findViewById(R.id.curvedSurfaceHeight);
        headTotalHeight = findViewById(R.id.headTotalHeight);
        padHeight = findViewById(R.id.padHeight);
        // Get Buttons
        clearButton = findViewById(R.id.clearParams);
        exitButton = findViewById(R.id.backConfiguration);
        RMDQuestion = findViewById(R.id.editionQuestion2);
        // Get SeekBar and corresponding TextViews
        pointBar = findViewById(R.id.pointNumberBar);
        pointText = findViewById(R.id.pointNumberBarText);
        angleBar = findViewById(R.id.angleNumberBar);
        angleText = findViewById(R.id.angleNumberBarText);
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
            RMDCheck.setChecked(configurationSaver.getBooleanParams("randomData"));
            if (!configurationSaver.getBooleanParams("randomData")) {
                setDefaultFromSaver();
            }
            else {
                setRandomDataView();
                setDefaultFromSaver();
            }
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
                if (isChecked) {
                    final AlertDialog.Builder randomAlertBuilder = new AlertDialog.Builder(ConfigurationEdition.this)
                            .setCancelable(false)
                            .setTitle("Alert!")
                            .setMessage("Are you sure to use random data?\n" +
                                    "All Parameters will be cleared.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    randomDialog.dismiss();
                                    setRandomDataView();
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
                else {
                    recoverFromRandomDataState();
                    setDefaultBlank();
                }
            }
        });
        //
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
        // clearButton click listener
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultBlank();
            }
        });
        // exitbutton click listener
        // Check if any error exists
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!paramsInspection()) {
                    Toast.makeText(ConfigurationEdition.this, "Error Saving! Please Check", Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    updateParams();
                    startActivity(new Intent(ConfigurationEdition.this, ConfigurationActivity.class));
                    finish();
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
        if (!configurationSaver.getBooleanParams("randomData")) {
            typeChoose = configurationSaver.getIntParams("typeNum");
            // Move the chosen type to the first position
            String typeChosen = typeList.get(typeChoose);
            String firstOne = typeList.get(0);
            if (!typeChosen.equals(firstOne)) {
                typeList.set(0, typeChosen);
                typeList.set(typeChoose, firstOne);
            }
            // Set NonstdCheck and two edittexts based on if its checked state
            NonstdCheck.setChecked(configurationSaver.getBooleanParams("nonStdHead"));
            if (NonstdCheck.isChecked()) {
                setNonStdText(true);
                ConcaveDeviation.setText(String.valueOf(configurationSaver.getFloatParams("concave")));
                ConvexDeviation.setText(String.valueOf(configurationSaver.getFloatParams("convex")));
            } else {
                setNonStdText(false);
            }
            // Set other views
            headInsideRadius.setText(String.valueOf(configurationSaver.getFloatParams("headRadius")));
            curvedSurfaceHeight.setText(String.valueOf(configurationSaver.getFloatParams("curvedSurface")));
            headTotalHeight.setText(String.valueOf(configurationSaver.getFloatParams("headTotal")));
            padHeight.setText(String.valueOf(configurationSaver.getFloatParams("padHeight")));
            EDCheck.setChecked(configurationSaver.getBooleanParams("ellipicityDetection"));
            RMDCheck.setChecked(configurationSaver.getBooleanParams("randomData"));
            pointBar.setProgress(configurationSaver.getIntParams("pointsProgress"));
            pointText.setText(String.valueOf(configurationSaver.getIntParams("pointsProgress")));
            angleBar.setProgress(configurationSaver.getIntParams("angleProgress"));
            angleText.setText(String.valueOf(configurationSaver.getIntParams("angleProgress")));
        }
        else {
            setRandomDataView();
            headInsideRadius.setText(String.valueOf(configurationSaver.getFloatParams("stdRadius")));
            curvedSurfaceHeight.setText(String.valueOf(configurationSaver.getFloatParams("minRadius")));
            headTotalHeight.setText(String.valueOf(configurationSaver.getFloatParams("maxRadius")));
            RMDCheck.setChecked(configurationSaver.getBooleanParams("randomData"));
            pointBar.setProgress(configurationSaver.getIntParams("pointsProgress"));
            pointText.setText(String.valueOf(configurationSaver.getIntParams("pointsProgress")));
            angleBar.setProgress(configurationSaver.getIntParams("angleProgress"));
            angleText.setText(String.valueOf(configurationSaver.getIntParams("angleProgress")));
        }
    }

    private void setDefaultBlank(){
        // NonStandard Head Default State: false
        NonstdCheck.setChecked(false);
        // ConcaveDeviation ConvexDeviation cannot be edited if the Nonstdcheck is false
        setNonStdText(NonstdCheck.isChecked());
        validateCheckBox(NonstdCheck);
        validateCheckBox(EDCheck);
        // Default EDCheck: true
        EDCheck.setChecked(true);
        // Default RMDCheck: false
        RMDCheck.setChecked(false);
        // Clear Contents
        headInsideRadius.setText("");
        headInsideRadius.setHint("Enter Head Radius Inside(mm)");
        curvedSurfaceHeight.setText("");
        curvedSurfaceHeight.setHint("Enter Curved Surface Height(mm)");
        headTotalHeight.setText("");
        headTotalHeight.setHint("Enter Head Total Height(mm)");
        padHeight.setText("");
        padHeight.setHint("Enter the Pad Height(mm)");
    }

    private void setRandomDataView() {
        invalidateCheckBox(NonstdCheck);
        setNonStdText(false);
        invalidateCheckBox(EDCheck);
        invalidateEditText(padHeight);
        headInsideRadius.setHint("Enter the Standard Radius(mm)");
        curvedSurfaceHeight.setHint("Enter the Minimum Radius(mm)");
        headTotalHeight.setHint("Enter the Maximum Radius(mm)");
    }

    private void recoverFromRandomDataState() {
        validateCheckBox(NonstdCheck);
        validateCheckBox(EDCheck);
        validateEditText(padHeight);
    }

    private void validateCheckBox(CheckBox checkBox) {
        checkBox.setTextColor(getResources().getColor(R.color.colorBlack));
        checkBox.setClickable(true);
    }

    private void invalidateCheckBox(CheckBox checkBox) {
        checkBox.setChecked(false);
        checkBox.setClickable(false);
        checkBox.setTextColor(Color.parseColor("#C2C2C2"));
    }

    private void validateEditText(EditText editText) {
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setTextColor(getResources().getColor(R.color.colorBlack));
        editText.setTextSize(16);
    }

    private void invalidateEditText(EditText editText) {
        editText.setError(null, null);
        editText.setText(R.string.Unavailable_Random);
        editText.setTextColor(Color.parseColor("#C2C2C2"));
        editText.setEnabled(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }

    private void updateParams(){
        if (!RMDCheck.isChecked()) {
            configurationSaver.addParamsUnrandom(typeChoose, NonstdCheck.isChecked(),
                    NonstdCheck.isChecked() ? Float.parseFloat(ConcaveDeviation.getText().toString()) : 0,
                    NonstdCheck.isChecked() ? Float.parseFloat(ConvexDeviation.getText().toString()) : 0,
                    Float.parseFloat(headInsideRadius.getText().toString()),
                    Float.parseFloat(curvedSurfaceHeight.getText().toString()), Float.parseFloat(headTotalHeight.getText().toString()),
                    EDCheck.isChecked(), Float.valueOf(String.valueOf(padHeight.getText())),
                    pointBar.getProgress(), angleBar.getProgress());
        }
        else {
            configurationSaver.addParamsRandom(Float.parseFloat(headInsideRadius.getText().toString()),
                    Float.parseFloat(curvedSurfaceHeight.getText().toString()),
                    Float.parseFloat(headTotalHeight.getText().toString()),
                    pointBar.getProgress(), angleBar.getProgress());
        }
    }

    private boolean paramsInspection() {
        isPassed = true;
        // In non-random data mode
        if (!RMDCheck.isChecked()) {
            // When Non-Standard Head is specified, parameters must be filled
            if (NonstdCheck.isChecked()) {
                if (isNullEmptyBlank(ConcaveDeviation.getText().toString())) {
                    isPassed = false;
                    ConcaveDeviation.setError("Needed!");
                }
                if (isNullEmptyBlank(ConvexDeviation.getText().toString())) {
                    isPassed = false;
                    ConvexDeviation.setError("Needed!");
                }
            }
            EditText[] editTexts = new EditText[]{headInsideRadius, curvedSurfaceHeight, headTotalHeight};
            for (int i = 0; i < editTexts.length; i++) {
                if (isNullEmptyBlank(editTexts[i].getText().toString())) {
                    isPassed = false;
                    editTexts[i].setError("This Parameter cannot be Null!");
                } else if (!isDoubleOrFloat(editTexts[i].getText().toString())) {
                    isPassed = false;
                    editTexts[i].setError("Input Parameter is Invalid!");
                }
            }
            if (!isNullEmptyBlank(padHeight.getText().toString())) {
                if (!isDoubleOrFloat(padHeight.getText().toString())) {
                    isPassed = false;
                    padHeight.setError("Input Parameter is Invalid!");
                }
            } else {
                isPassed = false;
                padHeight.setError("Please Specify the Pad Height");
            }
            return isPassed;
        }
        // In random data Mode
        else {
            // Three Essential Parameters Check
            EditText[] editTexts = new EditText[]{headInsideRadius, curvedSurfaceHeight, headTotalHeight};
            for (int i = 0; i < editTexts.length; i++) {
                if (isNullEmptyBlank(editTexts[i].getText().toString())) {
                    isPassed = false;
                    editTexts[i].setError("This Parameter cannot be Null!");
                } else if (!isDoubleOrFloat(editTexts[i].getText().toString())) {
                    isPassed = false;
                    editTexts[i].setError("Input Parameter is Invalid!");
                }
            }
            float stdRadius = Float.parseFloat(headInsideRadius.getText().toString());
            float minRadius = Float.parseFloat(curvedSurfaceHeight.getText().toString());
            float maxRadius = Float.parseFloat(headTotalHeight.getText().toString());
            if (minRadius > maxRadius) {
                isPassed = false;
                curvedSurfaceHeight.setError("This Cannot be larger than Maximum!");
            }
            if (minRadius > stdRadius) {
                isPassed = false;
                curvedSurfaceHeight.setError("This Cannot be larger than Standard!");
            }
            if (maxRadius < stdRadius) {
                isPassed = false;
                curvedSurfaceHeight.setError("This Cannot be larger than Maximum!");
            }
            return isPassed;
        }
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
