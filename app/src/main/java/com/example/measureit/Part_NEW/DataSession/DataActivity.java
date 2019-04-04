package com.example.measureit.Part_NEW.DataSession;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.measureit.MainActivity;
import com.example.measureit.MyClass.DataSaver;
import com.example.measureit.Part_RECORD.RecordActivity;
import com.example.measureit.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class DataActivity extends AppCompatActivity {

    private LineChartView lineChartView;
    private List<PointValue> originalDataPointValues = new ArrayList<>();
    private List<PointValue> convertedDataPointValues = new ArrayList<>();
    private List<PointValue> filteredDataPointValues = new ArrayList<>();
    private FilterProperties filterProperties;
    private LineChartProperties lineChartProperties;
    private DataSaver dataSaver;
    private String dataSaverName;
    private List<Float> xValueList;
    private List<Float> yValueList;
    private List<Float> xValueList_Converted;
    private List<Float> yValueList_Converted;
    private List<Float> xValueList_Filtered;
    private List<Float> yValueList_Filtered;
    private float veriticalMoveFactor = (float) 0.1;
    private float horizontalMoveFactor = (float) 0.1;
    private float rotateAngleFactor = 1;
    private float rotateCenterCoordinateX = 0;
    private float rotateCenterCoordinateY = 0;
    private float scaleFactor = (float) 0.1;
    private List<DataConfigItem> dataConfigItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        dataSaverName = getIntent().getStringExtra("dataSaverName");
        Button backLastButton = findViewById(R.id.backLastPage);
        Button backHomeButton = findViewById(R.id.backHomepage);
        if (getIntent().getBooleanExtra("isBackable", false)) {
            backLastButton.setVisibility(View.VISIBLE);
            backHomeButton.setVisibility(View.INVISIBLE);
            backLastButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DataActivity.this, RecordActivity.class));
                }
            });
        }
        else {
            backLastButton.setVisibility(View.INVISIBLE);
            backHomeButton.setVisibility(View.VISIBLE);
            backHomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DataActivity.this, MainActivity.class));
                }
            });
        }
        lineChartView = findViewById(R.id.dataChart);
        ListView listView = findViewById(R.id.dataConfigMenu);
        Button moveChartUp = findViewById(R.id.data_moveup);
        Button moveChartDown = findViewById(R.id.data_movedown);
        Button moveChartLeft = findViewById(R.id.data_moveleft);
        Button moveChartRight = findViewById(R.id.data_moveright);
        Button rotateChartLeft = findViewById(R.id.data_rotateleft);
        Button rotateChartRight = findViewById(R.id.data_rotateright);
        Button scaleChartUp = findViewById(R.id.data_scaleup);
        Button scaleChartDown = findViewById(R.id.data_scaledown);
        Button rollBackChart = findViewById(R.id.data_rollback);
        dataSaver = new DataSaver();
        dataSaver.saverInit(getApplicationContext(), "output");
        filterProperties = new FilterProperties(3, "none", "onlyOriginal");
        lineChartProperties = new LineChartProperties();
        drawOriginalLineChart();
        initConfigMenuItems();
        DataConfigAdapter dataConfigAdapter = new DataConfigAdapter(DataActivity.this, R.layout.result_data_listviewitem, dataConfigItems);
        listView.setAdapter(dataConfigAdapter);
        moveChartUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Move Up!", Toast.LENGTH_SHORT).show();
                moveChartByGivenParams("up");
            }
        });
        moveChartDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Move Down!", Toast.LENGTH_SHORT).show();
                moveChartByGivenParams("down");
            }
        });
        moveChartLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Move Left!", Toast.LENGTH_SHORT).show();
                moveChartByGivenParams("left");
            }
        });
        moveChartRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Move Right!", Toast.LENGTH_SHORT).show();
                moveChartByGivenParams("right");
            }
        });
        scaleChartUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Scale Up!", Toast.LENGTH_SHORT).show();
                scaleChartByGivenParams("up");
            }
        });
        scaleChartDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Scale Down!", Toast.LENGTH_SHORT).show();
                scaleChartByGivenParams("down");
            }
        });
        rotateChartLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Rotate Left!", Toast.LENGTH_SHORT).show();
                rotateChartByGivenParams("left");
            }
        });
        rotateChartRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Rotate Right!", Toast.LENGTH_SHORT).show();
                rotateChartByGivenParams("right");
            }
        });
        rollBackChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Original Chart!", Toast.LENGTH_SHORT).show();
                drawOriginalLineChart();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (dataConfigItems.get(position).getItemName()) {
                    case "Filter Type":
                        showFilterOptionDialog();
                        break;
                    case "Filter Display Mode":
                        showFilterDisplayModeDialog();
                        break;
                    case "Set Move Step Size":
                        showSeekBarChangeDialog("moveStep", (float) 0.01);
                        break;
                    case "Set Rotate Angle Size":
                        showSeekBarChangeDialog("rotateAngle", (float) 0.01);
                        break;
                    case "Set Rotate Center Coordinate":
                        showRotateCenterChangeDialog();
                        break;
                    case "Set Scale Step Size":
                        showSeekBarChangeDialog("scaleStep", (float) 0.01);
                        break;
                    case "Chart Axes Position":
                        showAxesPositionChangeDialog();
                        break;
                    case "Chart Dots Display Preferences":
                        showDotPreferenceChangeDialog();
                        break;
                    case "Chart Dots Custom Color":
                        showColorCustomDialog();
                        break;
                    case "Chart Grid Mode":
                        showGridModeChangeDialog();
                        break;
                    case "Save Data as…":
                        break;
                }
            }
        });
    }

    private void drawOriginalLineChart() {
        List<Float> angleList = dataSaver.readAngleData(dataSaverName);
        List<Float> rangeList = dataSaver.readRangeData(dataSaverName);
        originalDataPointValues = new ArrayList<>();
        xValueList = new ArrayList<>();
        yValueList = new ArrayList<>();
        if (angleList.size() == rangeList.size()) {
            for (int i = 0; i < angleList.size(); i++) {
                xValueList.add((float) (Math.cos(Math.PI*angleList.get(i)/180)*rangeList.get(i)));
                yValueList.add((float) (Math.sin(Math.PI*angleList.get(i)/180)*rangeList.get(i)));
                originalDataPointValues.add(new PointValue(xValueList.get(i), yValueList.get(i)));
            }
        }
        initLineChart();
    }

    private void drawFilteredLineChart() {
        List<Float> angleList = dataSaver.readAngleData(dataSaverName);
        List<Float> rangeList = dataSaver.readRangeData(dataSaverName);
        List<Float> filteredRangeList = new ArrayList<>();
        filteredDataPointValues = new ArrayList<>();
        xValueList_Filtered = new ArrayList<>();
        yValueList_Filtered = new ArrayList<>();
        if (angleList.size() == rangeList.size()) {
            filteredRangeList.add(rangeList.get(0));
            for (int i = 1; i < xValueList.size()-1; i++) {
                List<Float> tempList = new ArrayList<>();
                for (int j = -filterProperties.getFilterKernal()/2; j < filterProperties.getFilterKernal()/2; j++) {
                    tempList.add(rangeList.get(i+j));
                }
                Collections.sort(tempList);
                filteredRangeList.add(tempList.get(filterProperties.getFilterKernal()/2));
            }
            filteredRangeList.add(rangeList.get(rangeList.size()-1));
        }
        for (int i = 0; i < filteredRangeList.size(); i++) {
            xValueList_Filtered.add((float) (Math.cos(Math.PI*angleList.get(i)/180)*filteredRangeList.get(i)));
            yValueList_Filtered.add((float) (Math.sin(Math.PI*angleList.get(i)/180)*filteredRangeList.get(i)));
            filteredDataPointValues.add(new PointValue(xValueList_Filtered.get(i), yValueList_Filtered.get(i)));
        }
        initLineChart();
    }

    private void initLineChart() {
        LineChartData data = new LineChartData();
        List<Line> lines = new ArrayList<>();
        switch (filterProperties.getFilterDisplayMode()) {
            case "onlyOriginal":
                Line orginalLine = new Line(originalDataPointValues).setColor(Color.parseColor("#FFCD41"));
                switch (lineChartProperties.getLinePointShape()) {
                    case "circle":
                        orginalLine.setShape(ValueShape.CIRCLE);
                        break;
                    case "diamond":
                        orginalLine.setShape(ValueShape.DIAMOND);
                        break;
                    case "square":
                        orginalLine.setShape(ValueShape.SQUARE);
                        break;
                }
                orginalLine.setCubic(true);
                orginalLine.setPointRadius(lineChartProperties.getLinePointRadius());
                orginalLine.setFilled(lineChartProperties.getLineFilled());
                orginalLine.setHasLabels(false);
                orginalLine.setHasLabelsOnlyForSelected(true);
                orginalLine.setHasLines(lineChartProperties.getLinePointHasLine());
                orginalLine.setHasPoints(true);
                lines.add(orginalLine);
                data.setLines(lines);
                break;
            case "onlyFiltered":
                Line filteredLine = new Line(filteredDataPointValues).setColor(Color.parseColor("#000000"));
                switch (lineChartProperties.getLinePointShape()) {
                    case "circle":
                        filteredLine.setShape(ValueShape.CIRCLE);
                        break;
                    case "diamond":
                        filteredLine.setShape(ValueShape.DIAMOND);
                        break;
                    case "square":
                        filteredLine.setShape(ValueShape.SQUARE);
                        break;
                }
                filteredLine.setCubic(true);
                filteredLine.setPointRadius(lineChartProperties.getLinePointRadius());
                filteredLine.setFilled(lineChartProperties.getLineFilled());
                filteredLine.setHasLabels(false);
                filteredLine.setHasLabelsOnlyForSelected(true);
                filteredLine.setHasLines(lineChartProperties.getLinePointHasLine());
                filteredLine.setHasPoints(true);
                lines.add(filteredLine);
                data.setLines(lines);
                break;
            case "mixed":
                Line mixedOriginalLine = new Line(originalDataPointValues).setColor(Color.parseColor("#FFCD41"));
                Line mixedFilteredLine = new Line(filteredDataPointValues).setColor(Color.parseColor("#000000"));
                switch (lineChartProperties.getLinePointShape()) {
                    case "circle":
                        mixedOriginalLine.setShape(ValueShape.CIRCLE);
                        mixedFilteredLine.setShape(ValueShape.CIRCLE);
                        break;
                    case "diamond":
                        mixedOriginalLine.setShape(ValueShape.DIAMOND);
                        mixedFilteredLine.setShape(ValueShape.DIAMOND);
                        break;
                    case "square":
                        mixedOriginalLine.setShape(ValueShape.SQUARE);
                        mixedFilteredLine.setShape(ValueShape.SQUARE);
                        break;
                }
                mixedOriginalLine.setCubic(true);
                mixedFilteredLine.setCubic(true);
                mixedOriginalLine.setPointRadius(lineChartProperties.getLinePointRadius());
                mixedFilteredLine.setPointRadius(lineChartProperties.getLinePointRadius());
                mixedOriginalLine.setFilled(lineChartProperties.getLineFilled());
                mixedFilteredLine.setFilled(lineChartProperties.getLineFilled());
                mixedOriginalLine.setHasLabels(false);
                mixedFilteredLine.setHasLabels(false);
                mixedOriginalLine.setHasLabelsOnlyForSelected(true);
                mixedFilteredLine.setHasLabelsOnlyForSelected(true);
                mixedOriginalLine.setHasLines(lineChartProperties.getLinePointHasLine());
                mixedFilteredLine.setHasLines(lineChartProperties.getLinePointHasLine());
                mixedOriginalLine.setHasPoints(true);
                mixedFilteredLine.setHasPoints(true);
                lines.add(mixedOriginalLine);
                lines.add(mixedFilteredLine);
                data.setLines(lines);
                break;
        }
        // Coordinate Axis Setting - X
        Axis axisX = new Axis();
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.BLACK);
        axisX.setName("X(mm)");
        if (lineChartProperties.getLineXPositionBottom()) {
            data.setAxisXBottom(axisX);
        } else {
            data.setAxisXTop(axisX);
        }
        axisX.setHasLines(lineChartProperties.getLineXHasLines());
        axisX.setInside(true);
        // Coordinate Axis Setting - Y
        Axis axisY = new Axis();
        axisY.setName("Y(mm)");
        axisY.setTextColor(Color.BLACK);
        axisY.setTextSize(10);
        axisY.setHasLines(lineChartProperties.getLineYHasLines());
        axisY.setInside(true);
        if (lineChartProperties.getLineYPositionLeft()) {
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisYRight(axisY);
        }
        // Line Setting
        lineChartView.setInteractive(true);
        lineChartView.setFocusableInTouchMode(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChartView.setMaxZoom((float) 4);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setLineChartData(data);
        lineChartView.setVisibility(View.VISIBLE);
        // Viewport v = new Viewport(lineChartView.getMaximumViewport());
        // lineChartView.setCurrentViewport(v);
        lineChartView.startDataAnimation();
    }

    private void initConfigMenuItems() {
        dataConfigItems.add(new DataConfigItem(true, "Filter"));
        dataConfigItems.add(new DataConfigItem(false, "Filter Type"));
        dataConfigItems.add(new DataConfigItem(false, "Filter Display Mode"));
        dataConfigItems.add(new DataConfigItem(true, "Chart Control Preferences"));
        dataConfigItems.add(new DataConfigItem(false, "Set Move Step Size"));
        dataConfigItems.add(new DataConfigItem(false, "Set Rotate Angle Size"));
        dataConfigItems.add(new DataConfigItem(false, "Set Rotate Center Coordinate"));
        dataConfigItems.add(new DataConfigItem(false, "Set Scale Step Size"));
        dataConfigItems.add(new DataConfigItem(true, "Chart Display Preferences"));
        dataConfigItems.add(new DataConfigItem(false, "Chart Axes Position"));
        dataConfigItems.add(new DataConfigItem(false, "Chart Dots Display Preferences"));
        dataConfigItems.add(new DataConfigItem(false, "Chart Dots Custom Color"));
        dataConfigItems.add(new DataConfigItem(false, "Chart Grid Mode"));
        dataConfigItems.add(new DataConfigItem(true, "Publish and Share"));
        dataConfigItems.add(new DataConfigItem(false, "Save Data as…"));
        dataConfigItems.add(new DataConfigItem(true, ""));
    }

    private void moveChartByGivenParams(@NonNull String mode) {
        float tempHorizontalMoveFactor = horizontalMoveFactor;
        float tempVerticalMoveFactor = veriticalMoveFactor;
        switch (mode) {
            case "up":
                tempHorizontalMoveFactor = 0;
                break;
            case "down":
                tempHorizontalMoveFactor = 0;
                tempVerticalMoveFactor = -tempVerticalMoveFactor;
                break;
            case "left":
                tempVerticalMoveFactor = 0;
                tempHorizontalMoveFactor = -tempHorizontalMoveFactor;
                break;
            case "right":
                tempVerticalMoveFactor = 0;
                break;
        }
        if (xValueList.size()==yValueList.size()) {
            for (int i = 0; i < xValueList.size(); i++) {
                xValueList.set(i, xValueList.get(i) + tempHorizontalMoveFactor);
                yValueList.set(i, yValueList.get(i) + tempVerticalMoveFactor);
                originalDataPointValues.set(i, new PointValue(xValueList.get(i), yValueList.get(i)));
            }
            initLineChart();
        }
    }

    private void rotateChartByGivenParams(@NonNull String mode) {
        float tempRotateAngleFactor = rotateAngleFactor;
        switch (mode) {
            case "left":
                break;
            case "right":
                tempRotateAngleFactor = -rotateAngleFactor;
                break;
        }
        if (xValueList.size()==yValueList.size()) {
            tempRotateAngleFactor = (float) (Math.PI * tempRotateAngleFactor / 180.0);
            for (int i = 0; i < xValueList.size(); i++) {
                xValueList.set(i,
                        (float) ((xValueList.get(i)-rotateCenterCoordinateX)*Math.cos(tempRotateAngleFactor)-
                                (yValueList.get(i)-rotateCenterCoordinateY)*Math.sin(tempRotateAngleFactor)+
                                rotateCenterCoordinateX));
                yValueList.set(i,
                        (float) ((yValueList.get(i)-rotateCenterCoordinateY)*Math.cos(tempRotateAngleFactor)+
                                (xValueList.get(i)-rotateCenterCoordinateX)*Math.sin(tempRotateAngleFactor)+
                                rotateCenterCoordinateY));
                originalDataPointValues.set(i, new PointValue(xValueList.get(i), yValueList.get(i)));
            }
            initLineChart();
        }
    }

    private void scaleChartByGivenParams(@NonNull String mode) {
        float tempScaleFactor = scaleFactor;
        switch (mode) {
            case "up":
                break;
            case "down":
                tempScaleFactor = -tempScaleFactor;
                break;
        }
        if (xValueList.size()==yValueList.size()) {
            for (int i = 0; i < xValueList.size(); i++) {
                xValueList.set(i, xValueList.get(i) * (1 + tempScaleFactor/100));
                yValueList.set(i, yValueList.get(i) * (1 + tempScaleFactor/100));
                originalDataPointValues.set(i, new PointValue(xValueList.get(i), yValueList.get(i)));
            }
            initLineChart();
        }
    }

    private void showFilterOptionDialog() {
        final Dialog filterOptionDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        filterOptionDialog.setCancelable(true);
        filterOptionDialog.setCanceledOnTouchOutside(true);
        Window window = filterOptionDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_filtertype, null);
        view.findViewById(R.id.result_data_dialog_filter_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterOptionDialog.dismiss();
            }
        });
        view.findViewById(R.id.result_data_dialog_filter_median).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProperties.setFilterMode("median");
                filterOptionDialog.dismiss();
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        filterOptionDialog.show();
    }

    private void showFilterDisplayModeDialog() {
        final Dialog filterDisplayModeDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        filterDisplayModeDialog.setCanceledOnTouchOutside(true);
        filterDisplayModeDialog.setCancelable(true);
        Window window = filterDisplayModeDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_filterdisplaymode, null);
        view.findViewById(R.id.result_data_dialog_filter_onlyoriginal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProperties.setFilterDisplayMode("onlyOriginal");
                drawOriginalLineChart();
                filterDisplayModeDialog.dismiss();
            }
        });
        view.findViewById(R.id.result_data_dialog_filter_onlyfiltered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProperties.setFilterDisplayMode("onlyFiltered");
                drawFilteredLineChart();
                filterDisplayModeDialog.dismiss();
            }
        });
        view.findViewById(R.id.result_data_dialog_filter_mixed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProperties.setFilterDisplayMode("mixed");
                filterDisplayModeDialog.dismiss();
                drawFilteredLineChart();
            }
        });
        view.findViewById(R.id.result_data_dialog_filtermode_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDisplayModeDialog.dismiss();
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        filterDisplayModeDialog.show();
    }

    private void showSeekBarChangeDialog(@NonNull final String mode, final float seekBar2TextFactor) {
        final Dialog paramChangeDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        paramChangeDialog.setCancelable(true);
        paramChangeDialog.setCanceledOnTouchOutside(true);
        Window window = paramChangeDialog .getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_progress, null);
        window.setContentView(view);
        final SeekBar seekBar = view.findViewById(R.id.result_data_dialog_seekbar);
        final TextView titleText = view.findViewById(R.id.result_data_dialog_title);
        final TextView progressText = view.findViewById(R.id.result_data_dialog_text);
        progressText.setText(String.valueOf(seekBar.getProgress()*seekBar2TextFactor));
        switch (mode) {
            case "rotateAngle":
                titleText.setText("Slide to Change Rotate Angle Step(Degree)");
                seekBar.setProgress((int) (rotateAngleFactor/seekBar2TextFactor));
                progressText.setText(String.valueOf(Math.round(seekBar.getProgress()*seekBar2TextFactor*10)/10.0));
                break;
            case "moveStep":
                titleText.setText("Slide to Change Move Step(mm)");
                seekBar.setProgress((int) (horizontalMoveFactor/seekBar2TextFactor));
                progressText.setText(String.valueOf(Math.round(seekBar.getProgress()*seekBar2TextFactor*10)/10.0));
                break;
            case "scaleStep":
                titleText.setText("Slide to Change Scale Step(1+x%)");
                seekBar.setProgress((int) (scaleFactor/seekBar2TextFactor));
                progressText.setText(String.valueOf(Math.round(seekBar.getProgress()*seekBar2TextFactor*10)/10.0));
                break;
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // progress is from 0 to 100; transfer progress into factor and show it in textview
                progressText.setText(String.valueOf(Math.round(progress*seekBar2TextFactor*100)/100.0));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        view.findViewById(R.id.result_data_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramChangeDialog.dismiss();
            }
        });
        view.findViewById(R.id.result_data_dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case "rotateAngle":
                        rotateAngleFactor = Float.valueOf(progressText.getText().toString());
                        break;
                    case "moveStep":
                        horizontalMoveFactor = Float.valueOf(progressText.getText().toString());
                        veriticalMoveFactor = horizontalMoveFactor;
                        break;
                    case "scaleStep":
                        scaleFactor = Float.valueOf(progressText.getText().toString());
                        break;
                }
                paramChangeDialog.dismiss();
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        paramChangeDialog.show();
    }

    private void showRotateCenterChangeDialog() {
        final AlertDialog centerDialog = new AlertDialog.Builder(DataActivity.this).create();
        centerDialog.setView(LayoutInflater.from(DataActivity.this).inflate(R.layout.result_data_dialog_rotatecenter, null));
        centerDialog.setCanceledOnTouchOutside(true);
        centerDialog.show();
        centerDialog.getWindow().setContentView(R.layout.result_data_dialog_rotatecenter);
        final EditText editText_x = centerDialog.findViewById(R.id.center_coordinator_x);
        final EditText editText_y = centerDialog.findViewById(R.id.center_coordinator_y);
        editText_x.setHint(String.valueOf("X: "+rotateCenterCoordinateX));
        editText_y.setHint(String.valueOf("Y: "+rotateCenterCoordinateY));
        Button confirmButton = centerDialog.findViewById(R.id.result_data_center_confirm);
        Button cancelButton = centerDialog.findViewById(R.id.result_data_center_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerDialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNullEmptyBlank(editText_x.getText().toString())) {
                    rotateCenterCoordinateX = Float.valueOf(editText_x.getText().toString());
                }
                if (!isNullEmptyBlank(editText_y.getText().toString())) {
                    rotateCenterCoordinateY = Float.valueOf(editText_y.getText().toString());
                }
                centerDialog.dismiss();
                Toast.makeText(DataActivity.this, "Set Center as ("+rotateCenterCoordinateX+","+rotateCenterCoordinateY+")",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAxesPositionChangeDialog() {
        final Dialog positionChangeDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        positionChangeDialog.setCanceledOnTouchOutside(true);
        positionChangeDialog.setCancelable(true);
        Window window = positionChangeDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_axesposition, null);
        window.setContentView(view);
        Button xtop  = view.findViewById(R.id.result_data_dialog_xposition_top);
        Button xbottom = view.findViewById(R.id.result_data_dialog_xposition_bottom);
        Button yleft = view.findViewById(R.id.result_data_dialog_yposition_left);
        Button yright = view.findViewById(R.id.result_data_dialog_yposition_right);
        if (lineChartProperties.getLineXPositionBottom()) {
            xbottom.setTextColor(getResources().getColor(R.color.colorWhite));
            xbottom.setBackgroundResource(R.drawable.button_right_selected);
            xtop.setBackgroundResource(R.drawable.button_left_unselected);
        }
        else {
            xtop.setTextColor(getResources().getColor(R.color.colorWhite));
            xbottom.setBackgroundResource(R.drawable.button_right_unselected);
            xtop.setBackgroundResource(R.drawable.button_left_selected);
        }
        if (lineChartProperties.getLineYPositionLeft()) {
            yleft.setTextColor(getResources().getColor(R.color.colorWhite));
            yleft.setBackgroundResource(R.drawable.button_left_selected);
            yright.setBackgroundResource(R.drawable.button_right_unselected);
        }
        else {
            yright.setTextColor(getResources().getColor(R.color.colorWhite));
            yleft.setBackgroundResource(R.drawable.button_left_unselected);
            yright.setBackgroundResource(R.drawable.button_right_selected);
        }
        xtop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineChartProperties.getLineXPositionBottom()) {
                    lineChartProperties.setLineXPositionBottom(false);
                    initLineChart();
                    positionChangeDialog.dismiss();
                    showAxesPositionChangeDialog();
                }
            }
        });
        xbottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lineChartProperties.getLineXPositionBottom()) {
                    lineChartProperties.setLineXPositionBottom(true);
                    initLineChart();
                    positionChangeDialog.dismiss();
                    showAxesPositionChangeDialog();
                }
            }
        });
        yleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lineChartProperties.getLineYPositionLeft()) {
                    lineChartProperties.setLineYPositionLeft(true);
                    initLineChart();
                    positionChangeDialog.dismiss();
                    showAxesPositionChangeDialog();
                }
            }
        });
        yright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineChartProperties.getLineYPositionLeft()) {
                    lineChartProperties.setLineYPositionLeft(false);
                    initLineChart();
                    positionChangeDialog.dismiss();
                    showAxesPositionChangeDialog();
                }
            }
        });
        view.findViewById(R.id.result_data_dialog_axesposition_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionChangeDialog.dismiss();
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        positionChangeDialog.show();
    }

    private void showDotPreferenceChangeDialog() {
        final Dialog dotPreferenceChangeDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        dotPreferenceChangeDialog.setCanceledOnTouchOutside(true);
        dotPreferenceChangeDialog.setCancelable(true);
        Window window = dotPreferenceChangeDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_dotprefer, null);
        window.setContentView(view);
        Button shapeCircle = view.findViewById(R.id.result_data_dialog_dotshape_circle);
        Button shapeSqure = view.findViewById(R.id.result_data_dialog_dotshape_square);
        Button shapeDiamond = view.findViewById(R.id.result_data_dialog_dotshape_diamond);
        switch (lineChartProperties.getLinePointShape()) {
            case "circle":
                shapeCircle.setBackgroundResource(R.drawable.button_left_selected);
                shapeCircle.setTextColor(getResources().getColor(R.color.colorWhite));
                shapeSqure.setBackgroundResource(R.drawable.button_center_unselected);
                shapeDiamond.setBackgroundResource(R.drawable.button_right_unselected);
                break;
            case "square":
                shapeSqure.setBackgroundResource(R.drawable.button_center_selected);
                shapeSqure.setTextColor(getResources().getColor(R.color.colorWhite));
                shapeCircle.setBackgroundResource(R.drawable.button_left_unselected);
                shapeDiamond.setBackgroundResource(R.drawable.button_right_unselected);
                break;
            case "diamond":
                shapeDiamond.setBackgroundResource(R.drawable.button_right_selected);
                shapeDiamond.setTextColor(getResources().getColor(R.color.colorWhite));
                shapeCircle.setBackgroundResource(R.drawable.button_left_unselected);
                shapeSqure.setBackgroundResource(R.drawable.button_center_unselected);
                break;
        }
        shapeCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lineChartProperties.getLinePointShape().equals("circle")) {
                    lineChartProperties.setLinePointShape("circle");
                    dotPreferenceChangeDialog.dismiss();
                    initLineChart();
                    showDotPreferenceChangeDialog();
                }
            }
        });
        shapeSqure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lineChartProperties.getLinePointShape().equals("square")) {
                    lineChartProperties.setLinePointShape("square");
                    dotPreferenceChangeDialog.dismiss();
                    initLineChart();
                    showDotPreferenceChangeDialog();
                }
            }
        });
        shapeDiamond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lineChartProperties.getLinePointShape().equals("diamond")) {
                    lineChartProperties.setLinePointShape("diamond");
                    dotPreferenceChangeDialog.dismiss();
                    initLineChart();
                    showDotPreferenceChangeDialog();
                }
            }
        });
        final Button dotSize1 = view.findViewById(R.id.result_data_dialog_dotsize_1);
        final Button dotSize2 = view.findViewById(R.id.result_data_dialog_dotsize_2);
        final Button dotSize3 = view.findViewById(R.id.result_data_dialog_dotsize_3);
        final Button dotSize4 = view.findViewById(R.id.result_data_dialog_dotsize_4);
        final Button dotSize5 = view.findViewById(R.id.result_data_dialog_dotsize_5);
        Button[] dotSizeButtons = new Button[]{dotSize1, dotSize2, dotSize3, dotSize4, dotSize5};
        dotSizeButtons[lineChartProperties.getLinePointRadius()-1].setBackgroundResource(
                (lineChartProperties.getLinePointRadius()==1)?R.drawable.button_left_selected:
                        ((lineChartProperties.getLinePointRadius()==5)?R.drawable.button_right_selected:R.drawable.button_center_selected)
        );
        dotSizeButtons[lineChartProperties.getLinePointRadius()-1].setTextColor(getResources().getColor(R.color.colorWhite));
        dotSize1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineChartProperties.getLinePointRadius() != 1) {
                    lineChartProperties.setLinePointRadius(1);
                    initLineChart();
                    dotPreferenceChangeDialog.dismiss();
                    showDotPreferenceChangeDialog();
                }
            }
        });
        dotSize2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineChartProperties.getLinePointRadius() != 2) {
                    lineChartProperties.setLinePointRadius(2);
                    initLineChart();
                    dotPreferenceChangeDialog.dismiss();
                    showDotPreferenceChangeDialog();
                }
            }
        });
        dotSize3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineChartProperties.getLinePointRadius() != 3) {
                    lineChartProperties.setLinePointRadius(3);
                    initLineChart();
                    dotPreferenceChangeDialog.dismiss();
                    showDotPreferenceChangeDialog();
                }
            }
        });
        dotSize4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineChartProperties.getLinePointRadius() != 4) {
                    lineChartProperties.setLinePointRadius(4);
                    initLineChart();
                    dotPreferenceChangeDialog.dismiss();
                    showDotPreferenceChangeDialog();
                }
            }
        });
        dotSize5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineChartProperties.getLinePointRadius() != 5) {
                    lineChartProperties.setLinePointRadius(5);
                    initLineChart();
                    dotPreferenceChangeDialog.dismiss();
                    showDotPreferenceChangeDialog();
                }
            }
        });
        Switch dotLines = view.findViewById(R.id.result_data_dialog_dotprefer_dotlines);
        dotLines.setChecked(lineChartProperties.getLinePointHasLine());
        dotLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lineChartProperties.setLinePointHasLine(isChecked);
                dotPreferenceChangeDialog.dismiss();
                initLineChart();
                showDotPreferenceChangeDialog();
            }
        });
        Switch lineFilled = view.findViewById(R.id.result_data_dialog_dotprefer_linefilled);
        TextView textView = view.findViewById(R.id.result_data_dialog_dotprefer_linefilledtext);
        if (dotLines.isChecked()) {
            textView.setTextColor(getResources().getColor(R.color.colorBlack));
            lineFilled.setChecked(lineChartProperties.getLineFilled());
            lineFilled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    lineChartProperties.setLineFilled(isChecked);
                    dotPreferenceChangeDialog.dismiss();
                    initLineChart();
                    showDotPreferenceChangeDialog();
                }
            });
        }
        else {
            lineFilled.setEnabled(false);
            textView.setTextColor(getResources().getColor(R.color.colorDarkGray));
        }
        view.findViewById(R.id.result_data_dialog_dotprefer_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dotPreferenceChangeDialog.dismiss();
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dotPreferenceChangeDialog.show();
    }

    private void showGridModeChangeDialog() {
        final Dialog gridChangeDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        gridChangeDialog.setCanceledOnTouchOutside(true);
        gridChangeDialog.setCancelable(true);
        Window window = gridChangeDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_gridmode, null);
        window.setContentView(view);
        final Switch verticalSwitch = view.findViewById(R.id.result_data_dialog_gridmode_verticalswitch);
        final Switch horizontalSwitch = view.findViewById(R.id.result_data_dialog_gridmode_horizontalswitch);
        verticalSwitch.setChecked(lineChartProperties.getLineYHasLines());
        horizontalSwitch.setChecked(lineChartProperties.getLineXHasLines());
        verticalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lineChartProperties.setLineYHasLines(isChecked);
                initLineChart();
            }
        });
        horizontalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lineChartProperties.setLineXHasLines(isChecked);
                initLineChart();
            }
        });
        view.findViewById(R.id.result_data_dialog_gridmode_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridChangeDialog.dismiss();
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        gridChangeDialog.show();
    }

    private void showColorCustomDialog() {
        final Dialog colorCustomDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        colorCustomDialog.setCancelable(true);
        colorCustomDialog.setCanceledOnTouchOutside(true);
        Window window = colorCustomDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_dotcolor, null);
        window.setContentView(view);
        view.findViewById(R.id.result_data_dialog_dotcolor_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorCustomDialog.dismiss();
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        colorCustomDialog.show();
    }

    private boolean isNullEmptyBlank(@NonNull String str){
        return str == null || "".equals(str) || "".equals(str.trim());
    }
}
