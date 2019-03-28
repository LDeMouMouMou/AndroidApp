package com.example.measureit.Part_NEW;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.measureit.MyClass.BluetoothServer;
import com.example.measureit.MyClass.ConfigurationSaver;
import com.example.measureit.MyClass.DataCalculation;
import com.example.measureit.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class ScannerActivity extends AppCompatActivity {

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    // Defination of Realtime-Chart
    public LineChartView lineChartView;
    public List<PointValue> mPointValues = new ArrayList<>();
    public List<AxisValue> mAxisXValues = new ArrayList<>();
    // Defination of Layout View Group
    public ProgressBar progressBar;
    public TextView PrgressText;
    public TextView resultText;
    public Button startButton;
    public Button cancelButton;
    public Button backButton;
    public Button nextButton;
    // Defination of Flags
    public boolean isRandom;
    //
    public ConfigurationSaver configurationSaver = new ConfigurationSaver();
    public DataCalculation dataCalculation = new DataCalculation();
    public BluetoothServer bluetoothServer;
    public BluetoothServer.BLEBinder bleBinder;

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleBinder = (BluetoothServer.BLEBinder) service;
            bluetoothServer = bleBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothServer = null;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        String selectedConfigurationName = getIntent().getStringExtra("selectedConfigurationName");
        configurationSaver.configurationSaverInit(getApplicationContext(), true, selectedConfigurationName);
        // Find View Group
        lineChartView = findViewById(R.id.ScannerChart);
        progressBar = findViewById(R.id.scannerProgress);
        PrgressText = findViewById(R.id.progressText);
        resultText = findViewById(R.id.resultText);
        startButton = findViewById(R.id.button_start);
        cancelButton = findViewById(R.id.button_cancel);
        backButton = findViewById(R.id.button_back);
        nextButton = findViewById(R.id.button_next);
        // Only the Start Button can be viewed when created
        startButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        // Create a Async Task for Receiving Data
        final dataReceiver dataReceiver = new dataReceiver(ScannerActivity.this);
        bindService(new Intent(this, BluetoothServer.class), serviceConnection, Context.BIND_AUTO_CREATE);
        isRandom = configurationSaver.getBooleanParams("randomData");
        // Set ClickListener of Start Button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only when the task is pending can the Start Button validate
                // In fact, Start Button will not be viewed in other situation, just in case
                if (dataReceiver.getStatus()==AsyncTask.Status.PENDING) {
                    dataReceiver.execute();
                    startButton.setVisibility(View.INVISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                }
            }
        });
        // Set ClickListener of Cancel Button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only when the task is running can the Cancel Button validate
                // In fact, Cancel Button will not be viewed in other situation, just in case
                if (dataReceiver.getStatus()==AsyncTask.Status.RUNNING){
                    // Create an alert
                    final AlertDialog cancelAlert = new AlertDialog.Builder(ScannerActivity.this)
                            .setTitle("Task Cancellation Alert!")
                            .setMessage("This Measurement Task can be ONLY Executed ONCE! You CANNOT Undo this! " +
                                    "The Data will NOT be Saved!")
                            .setCancelable(false)
                            .setPositiveButton("Yes, I am SURE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Make the Back Button Visible
                                    startButton.setVisibility(View.INVISIBLE);
                                    backButton.setVisibility(View.VISIBLE);
                                    dataReceiver.cancel(true);

                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    cancelAlert.setCanceledOnTouchOutside(false);
                    cancelAlert.show();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only when the task is Cancelled can the Back Button validate
                // In fact, Back Button will not be viewed in other situation, just in case
                if (dataReceiver.isCancelled()) {
                    Intent backIntent = new Intent(ScannerActivity.this, ConfigurationActivity.class);
                    startActivity(backIntent);
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only when the task is finished can the Next Button validate
                // In fact, Next Button will not be viewed in other situation, just in case
                if (dataReceiver.getStatus() == AsyncTask.Status.FINISHED) {
                    Intent nextIntent = new Intent(ScannerActivity.this, ResultActivity.class);
                    startActivity(nextIntent);
                }
            }
        });
    }

    // Disable all key except Multi-Task, for protecting the scanner activity
    // Home Button cannot be disabled
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

    private void initLineChart(float xval, float yval){
        // mAxisXValues.add(new AxisValue(count).setValue(xval));
        mPointValues.add(new PointValue(xval, yval));
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));
        List<Line> lines = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(true);
        line.setPointRadius(2);
        line.setFilled(false);
        line.setHasLabels(false);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(false);
        line.setHasPoints(true);
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        // Coordinate Axis Setting - X
        Axis axisX = new Axis();
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.BLACK);
        axisX.setName("X(mm)");
        data.setAxisXBottom(axisX);
        axisX.setHasLines(true);
        axisX.setInside(true);
        // Coordinate Axis Setting - Y
        Axis axisY = new Axis();
        axisY.setName("Y(mm)");
        axisY.setTextColor(Color.BLACK);
        axisY.setTextSize(10);
        axisY.setHasLines(true);
        axisY.setInside(true);
        data.setAxisYLeft(axisY);
        // Line Setting
        lineChartView.setInteractive(true);
        lineChartView.setFocusableInTouchMode(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChartView.setMaxZoom((float)4);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setLineChartData(data);
        lineChartView.setVisibility(View.VISIBLE);
        // Viewport v = new Viewport(lineChartView.getMaximumViewport());
        // lineChartView.setCurrentViewport(v);
        lineChartView.startDataAnimation();
    }

    private class dataReceiver extends AsyncTask<String, Float, String>{
        private WeakReference<ScannerActivity> activityWeakReference;
        dataReceiver(ScannerActivity scannerActivity){
            this.activityWeakReference = new WeakReference<>(scannerActivity);
        }
        @Override
        protected String doInBackground(String... params){
            try {
                int totalCount = 20;
                int repeatCount = 5;
                float progressDisplay;
                float stdRadius = 10;
                dataCalculation.addStdRadius(stdRadius);
                // Angle Interval for Measuring Depends on the Total Points
                float angleInterval = (float) 180/(totalCount-1);
                dataCalculation.clearOriginalData();
                Thread.sleep(500);
                for (int i = 1; i <= totalCount; i++) {
                    float sval = 0;
                    // Check if the task is already cancelled, prevent it from memory leak
                    if (!isCancelled()) {
                        if (!isRandom) {
                            for (int j = 0; j <= repeatCount; j++) {
                                bluetoothServer.sendCommand("1");
                                Thread.sleep(200);
                                sval += bluetoothServer.getOneDistanceNumber();
                            }
                            sval = sval / repeatCount;
                        }
                        else {
                            sval = bluetoothServer.getOneRandomDistanceNumber(stdRadius);
                            Thread.sleep(500);
                        }
                        float xval = (float) (Math.cos(Math.PI * (i - 1) * angleInterval / 180) * sval);
                        float yval = (float) (Math.sin(Math.PI * (i - 1) * angleInterval / 180) * sval);
                        // Calculate Progress ?Percent
                        progressDisplay = (float) (i * 100.0 / totalCount);
                        // Update UI
                        publishProgress(progressDisplay, sval, xval, yval, (i - 1) * angleInterval);
                        // Too short sleep duration may cause fatal error
                        Thread.sleep(100);
                    }
                }
                } catch (InterruptedException e){
                    e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Float... params){
            dataCalculation.addData(params[1], params[2], params[3]);
            PrgressText.setText("Receiving Data..."+params[0]+"%");
            progressBar.setProgress(params[0].intValue());
            initLineChart(params[2], params[3]);
            // lsr[] includes Circle Center Coordinate (lsr[0], lsr[1]) and Radius(lsr[2])
            float[] lsr = dataCalculation.getLeastSquaresResult();
            String result = "Range: "+params[1]+"\n"
                    +"With Angle(degree) on: "+params[4]+"\n"
                    +"Average Range: "+dataCalculation.getAverage()+"\n"
                    +"Standard Deviation: "+dataCalculation.getStandardDiviation()+"\n"
                    +"Realtime Radius: "+lsr[2]+"\n"
                    +"Center on ("+lsr[0]+", "+lsr[1]+")";
            resultText.setText(result);
        }

        @Override
        protected void onPostExecute(String result){
            // Adding Weak Reference to Prevent Memory Leak
            ScannerActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()){
                return;
            }
            // After Measuring Session Done, Only the Next Button can be viewed
            startButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            backButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onCancelled(){
            PrgressText.setText("Measuring Cancelled");
            progressBar.setProgress(0);
        }
    }
}
