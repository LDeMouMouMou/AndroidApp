package com.example.measureit.Part_NEW.DataSession;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bravin.btoast.BToast;
import com.example.measureit.MainActivity;
import com.example.measureit.MyClass.DataCalculation;
import com.example.measureit.MyClass.DataSaver;
import com.example.measureit.Part_RECORD.RecordActivity;
import com.example.measureit.R;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class DataActivity extends AppCompatActivity {

    private LineChartView lineChartView;
    private List<PointValue> originalDataPointValues = new ArrayList<>();
    private List<PointValue> filteredDataPointValues = new ArrayList<>();
    private List<PointValue> standardDataPointValues = new ArrayList<>();
    private List<PointValue> fittingDataPointValues = new ArrayList<>();
    private FilterProperties filterProperties;
    private LineChartProperties lineChartProperties;
    private DataSaver dataSaver;
    private String dataSaverName;
    private DataCalculation originalDataCalculation;
    private DataCalculation filteredDataCalculation;
    private List<Float> xValueList;
    private List<Float> yValueList;
    private float veriticalMoveFactor = (float) 0.1;
    private float horizontalMoveFactor = (float) 0.1;
    private float rotateAngleFactor = 1;
    private float rotateCenterCoordinateX = 0;
    private float rotateCenterCoordinateY = 0;
    private float scaleFactor = (float) 0.1;
    private List<DataConfigItem> dataConfigItems = new ArrayList<>();
    private boolean convertButtonsFlag = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        dataSaverName = getIntent().getStringExtra("dataSaverName");
        BToast.Config.getInstance().apply(getApplication());
        Button backLastButton = findViewById(R.id.backLastPage);
        Button infoButton = findViewById(R.id.result_data_showInfo);
        backLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getBooleanExtra("isBackable", false)) {
                    startActivity(new Intent(DataActivity.this, RecordActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(DataActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });
        lineChartView = findViewById(R.id.dataChart);
        BouncyListView listView = findViewById(R.id.dataConfigMenu);
        final Button manifactureShow = findViewById(R.id.data_manifacture_show);
        Button moveChartUp = findViewById(R.id.data_moveup);
        Button moveChartDown = findViewById(R.id.data_movedown);
        Button moveChartLeft = findViewById(R.id.data_moveleft);
        Button moveChartRight = findViewById(R.id.data_moveright);
        Button rotateChartLeft = findViewById(R.id.data_rotateleft);
        Button rotateChartRight = findViewById(R.id.data_rotateright);
        Button scaleChartUp = findViewById(R.id.data_scaleup);
        Button scaleChartDown = findViewById(R.id.data_scaledown);
        Button rollBackChart = findViewById(R.id.data_rollback);
        final Button[] convertButtons = new Button[]{manifactureShow, moveChartUp, moveChartDown,
            moveChartLeft, moveChartRight, rotateChartLeft, rotateChartRight, scaleChartUp,
            scaleChartDown, rollBackChart};
        setConvertButtonsInvisible(convertButtons);
        dataSaver = new DataSaver();
        dataSaver.saverInit(getApplicationContext(), "output");
        originalDataCalculation = new DataCalculation();
        originalDataCalculation.clearOriginalData();
        filteredDataCalculation = new DataCalculation();
        filteredDataCalculation.clearOriginalData();
        filterProperties = new FilterProperties(3, "none", "onlyOriginal");
        lineChartProperties = new LineChartProperties();
        // getting data ready
        getOriginalDataList();
        getFilteredDataList();
        getStandardDataList();
        getFittingDataList(true);
        initConfigMenuItems();
        initLineChart();
        setLineChartLegends();
        DataConfigAdapter dataConfigAdapter = new DataConfigAdapter(DataActivity.this, R.layout.result_data_listviewitem, dataConfigItems);
        listView.setAdapter(dataConfigAdapter);
        manifactureShow.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                        case MotionEvent.ACTION_MOVE:
                            int move2X = (int) event.getRawX();
                            int move2Y = (int) event.getRawY();
                            int moveX = move2X - startX;
                            int moveY = move2Y - startY;
                            int button_left = 0;
                            int button_right = button_left + manifactureShow.getWidth();
                            int button_top = manifactureShow.getTop();
                            button_top += moveY;
                            // Constraint button moving space
                            if (button_top < lineChartView.getTop()) {
                                button_top = lineChartView.getTop();
                            }
                            if (button_top > lineChartView.getBottom() - manifactureShow.getHeight()) {
                                button_top = lineChartView.getBottom() - manifactureShow.getHeight();
                            }
                            int button_bottom = button_top + manifactureShow.getHeight();
                            manifactureShow.layout(button_left, button_top, button_right, button_bottom);
                            setConvertButtonsVisible(convertButtons);
                            startX = move2X;
                            startY = move2Y;
                            return false;
                    case MotionEvent.ACTION_UP:
                        return false;
                }
                return DataActivity.super.onTouchEvent(event);
            }
        });
        manifactureShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (convertButtonsFlag) {
                    setConvertButtonsInvisible(convertButtons);
                    convertButtonsFlag = false;
                }
                else {
                    setConvertButtonsVisible(convertButtons);
                    convertButtonsFlag = true;
                }
            }
        });
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
                getOriginalDataList();
                initLineChart();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (dataConfigItems.get(position).getItemName()) {
                    case "Filter Type":
                        showFilterOptionDialog();
                        break;
                    case "Fitting Type":
                        showFittingOptionDialog();
                        break;
                    case "Chart Line Display Selection":
                        showChartLineDisplaySelectionDialog();
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
                    case "Chart Line Display Preferences":
                        showLinePreferenceChangeDialog();
                        break;
                    case "Chart Dots/Line Custom Color":
                        showColorCustomDialog();
                        break;
                    case "Chart Grid Mode":
                        showGridModeChangeDialog();
                        break;
                    case "Save Data as…":
                        showSavingDataDialog();
                        break;
                }
            }
        });
    }

    private void setConvertButtonsInvisible(@NonNull Button[] buttons) {
        Button button0 = buttons[0];
        for (int i = 1; i < buttons.length; i++) {
            buttons[i].layout(button0.getLeft(), button0.getTop(), button0.getRight(), button0.getBottom());
            buttons[i].setVisibility(View.INVISIBLE);
        }
    }

    private void setConvertButtonsVisible(@NonNull Button[] buttons) {
        Button button0 = buttons[0];
        int spaceValue = dip2dx(12);
        for (int i = 1; i < buttons.length; i++) {
            buttons[i].setVisibility(View.VISIBLE);
            int left = buttons[i-1].getRight()+spaceValue;
            int right = left + buttons[i].getWidth();
            int top = button0.getTop();
            int bottom = top + buttons[i].getHeight();
            buttons[i].layout(left, top, right, bottom);
        }
    }

    private int dip2dx(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void getOriginalDataList() {
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
                originalDataCalculation.addData(rangeList.get(i), xValueList.get(i), yValueList.get(i));
            }
        }
    }

    private void getFilteredDataList() {
        List<Float> angleList = dataSaver.readAngleData(dataSaverName);
        List<Float> rangeList = dataSaver.readRangeData(dataSaverName);
        List<Float> filteredRangeList = new ArrayList<>();
        filteredDataPointValues = new ArrayList<>();
        List<Float> xValueList_Filtered = new ArrayList<>();
        List<Float> yValueList_Filtered = new ArrayList<>();
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
            filteredDataCalculation.addData(filteredRangeList.get(i), xValueList_Filtered.get(i), yValueList_Filtered.get(i));
        }
    }

    private void getStandardDataList() {
        float standardRange = dataSaver.getFloatParams("stdRadius", dataSaverName);
        List<Float> xValueList_Standard = new ArrayList<>();
        List<Float> yValueList_Standard = new ArrayList<>();
        for (int i = 0; i <= 1000; i++) {
            xValueList_Standard.add((float) (Math.cos(Math.PI / 1000 * i)*standardRange));
            yValueList_Standard.add((float) (Math.sin(Math.PI / 1000 * i)*standardRange));
            standardDataPointValues.add(new PointValue(xValueList_Standard.get(i), yValueList_Standard.get(i)));
        }
    }

    private void getFittingDataList(boolean usingOriginalOrNot) {
        float fittingResult[] = usingOriginalOrNot?
                originalDataCalculation.getLeastSquaresResult():filteredDataCalculation.getLeastSquaresResult();
        fittingDataPointValues = new ArrayList<>();
        List<Float> xValueList_Fitting = new ArrayList<>();
        List<Float> yValueList_Fitting = new ArrayList<>();
        for (int i = 0; i <= 1000; i++) {
            xValueList_Fitting.add((float) (Math.cos(Math.PI / 1000 * i) * fittingResult[2]) + fittingResult[0]);
            yValueList_Fitting.add((float) (Math.sin(Math.PI / 1000 * i) * fittingResult[2]) + fittingResult[1]);
            fittingDataPointValues.add(new PointValue(xValueList_Fitting.get(i), yValueList_Fitting.get(i)));
        }
    }

    private void showInfoDialog() {
        Dialog infoDialog = new Dialog(DataActivity.this, R.style.centerDialog);
        infoDialog.setCancelable(true);
        infoDialog.setCanceledOnTouchOutside(true);
        Window window = infoDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_info, null);
        TextView modeText = view.findViewById(R.id.result_data_dialog_info_mode);
        TextView pointsText = view.findViewById(R.id.result_data_dialog_info_points);
        TextView radiusText = view.findViewById(R.id.result_data_dialog_info_radius);
        TextView timeText = view.findViewById(R.id.result_data_dialog_info_time);
        TextView configurationText = view.findViewById(R.id.result_data_dialog_info_configuation);
        modeText.setText(dataSaver.getBooleanParams("randomData", dataSaverName)?"Randomly Simulated":"Real-Time");
        String pointsTextContent = "Total "+dataSaver.getIntParams("pointsProgress", dataSaverName)*
                dataSaver.getIntParams("angleProgress", dataSaverName)+" Points in 180 Degrees";
        pointsText.setText(pointsTextContent);
        String radiusTextContent = "Standard Radius: "+dataSaver.getFloatParams("stdRadius", dataSaverName)+
                " (from "+dataSaver.getFloatParams("minRadius", dataSaverName)+" to "+
                dataSaver.getFloatParams("maxRadius", dataSaverName)+")";
        radiusText.setText(radiusTextContent);
        String timeTextContent = "Created on "+dataSaverName.substring(dataSaverName.indexOf("@")+1);
        timeText.setText(timeTextContent);
        String configurationTextContent = "Using Configuration: "+dataSaverName.substring(0, dataSaverName.indexOf("@"));
        configurationText.setText(configurationTextContent);
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        infoDialog.show();
    }

    private void initLineChart() {
        LineChartData data = new LineChartData();
        List<Line> lines = new ArrayList<>();
        List<List<PointValue>> allPointValues = new ArrayList<>();
        final List<String> lineName = new ArrayList<>();
        allPointValues.add(originalDataPointValues);
        allPointValues.add(filteredDataPointValues);
        allPointValues.add(standardDataPointValues);
        allPointValues.add(fittingDataPointValues);
        Boolean[] hasLines = new Boolean[]{lineChartProperties.getLineHasOriginalLine(),
                lineChartProperties.getLineHasFilteredLine(),
                lineChartProperties.getLineHasStandardLine(),
                lineChartProperties.getLineHasFittingLine()
        };
        for (int i = 0; i < 4; i++) {
            if (hasLines[i]) {
                switch (i) {
                    case 0:
                        lineName.add("Original Data");
                        break;
                    case 1:
                        lineName.add("Filtered Data");
                        break;
                    case 2:
                        lineName.add("Standard Line");
                        break;
                    case 3:
                        lineName.add("Fitting Line");
                        break;
                }
                Line line = new Line(allPointValues.get(i)).setColor(Color.parseColor("#"+
                        lineChartProperties.getLineAllPointColorARGB()[i]));
                if (i == 0 || i == 1) {
                    switch (lineChartProperties.getLineChartPointShape(i)) {
                        case "circle":
                            line.setShape(ValueShape.CIRCLE);
                            break;
                        case "diamond":
                            line.setShape(ValueShape.DIAMOND);
                            break;
                        case "square":
                            line.setShape(ValueShape.SQUARE);
                            break;
                    }
                    line.setCubic(true)
                            .setPointRadius(lineChartProperties.getLineChartDotSize(i))
                            .setFilled(lineChartProperties.getLineChartDotHasFilled(i))
                            .setHasLines(lineChartProperties.getLineChartDotHasLines(i))
                            .setHasPoints(true)
                            .setHasLabels(false)
                            .setHasLabelsOnlyForSelected(true);
                }
                else {
                    line.setShape(ValueShape.CIRCLE);
                    line.setCubic(true)
                            .setPointRadius(lineChartProperties.getLineChartLineSize(i - 2))
                            .setFilled(lineChartProperties.getLineChartLineHasFilled(i - 2))
                            .setHasPoints(true)
                            .setHasLabels(false)
                            .setHasLabelsOnlyForSelected(true);
                }
                lines.add(line);
            }
        }
        data.setLines(lines);
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
        data.setValueLabelBackgroundEnabled(true);
        data.setValueLabelBackgroundAuto(false);
        data.setValueLabelBackgroundColor(getResources().getColor(R.color.colorBrightBlue));
        // Line Setting
        lineChartView.setScrollEnabled(true);
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
        lineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, PointValue pointValue) {
//                Toast.makeText(DataActivity.this, lineName.get(i)
//                                +" at x= "+String.valueOf(Math.round(pointValue.getX()*10000)/10000.0)
//                                +" y= "+String.valueOf(Math.round(pointValue.getY()*10000)/10000.0),
//                        Toast.LENGTH_SHORT).show();
                BToast.info(DataActivity.this).text(lineName.get(i)
                        +" at x= "+String.valueOf(Math.round(pointValue.getX()*1000)/1000.0)
                        +" y= "+String.valueOf(Math.round(pointValue.getY()*1000)/1000.0))
                        .animationGravity(Gravity.BOTTOM).show();
            }

            @Override
            public void onValueDeselected() {

            }
        });
        setLineChartLegends();
    }

    private void setLineChartLegends() {
        Button originalDotLegend = findViewById(R.id.result_data_legend_original);
        Button filteredDotLegend = findViewById(R.id.result_data_legend_filtered);
        Button standardLineLegend = findViewById(R.id.result_data_legend_standard);
        Button fittingLineLegend = findViewById(R.id.result_data_legend_fitting);
        Button[] buttons = new Button[]{originalDotLegend, filteredDotLegend, standardLineLegend, fittingLineLegend};
        TextView originalDotText = findViewById(R.id.result_data_legend_originalText);
        TextView filteredDotText = findViewById(R.id.result_data_legend_filteredText);
        TextView standardLineText = findViewById(R.id.result_data_legend_standardText);
        TextView fittingLineText = findViewById(R.id.result_data_legend_fittingText);
        TextView[] textViews = new TextView[]{originalDotText, filteredDotText, standardLineText, fittingLineText};
        GradientDrawable originalButtonBackground = (GradientDrawable) originalDotLegend.getBackground();
        GradientDrawable filteredButtonBackground = (GradientDrawable) filteredDotLegend.getBackground();
        GradientDrawable standardButtonBackground = (GradientDrawable) standardLineLegend.getBackground();
        GradientDrawable fittingButtonBackground = (GradientDrawable) fittingLineLegend.getBackground();
        originalButtonBackground.setColor(Color.parseColor("#"+lineChartProperties.getLineOriginalPointColorARGB()));
        filteredButtonBackground.setColor(Color.parseColor("#"+lineChartProperties.getLineFilteredPointColorARGB()));
        standardButtonBackground.setColor(Color.parseColor("#"+lineChartProperties.getLineStandardPointColorARGB()));
        fittingButtonBackground.setColor(Color.parseColor("#"+lineChartProperties.getLineFittingPointColorARGB()));
        originalDotText.setTextColor(getResources().getColor(lineChartProperties.getLineHasOriginalLine()?
                R.color.colorBlack:R.color.colorDarkGray));
        filteredDotText.setTextColor(getResources().getColor(lineChartProperties.getLineHasFilteredLine()?
                R.color.colorBlack:R.color.colorDarkGray));
        standardLineText.setTextColor(getResources().getColor(lineChartProperties.getLineHasStandardLine()?
                R.color.colorBlack:R.color.colorDarkGray));
        fittingLineText.setTextColor(getResources().getColor(lineChartProperties.getLineHasStandardLine()?
                R.color.colorBlack:R.color.colorDarkGray));
        for (int i = 0; i < 4; i++) {
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showColorCustomDialog();
                }
            });
            textViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChartLineDisplaySelectionDialog();
                }
            });
        }
    }

    private void initConfigMenuItems() {
        dataConfigItems.add(new DataConfigItem(true, "Filter & Fitting Line"));
        dataConfigItems.add(new DataConfigItem(false, "Filter Type"));
        dataConfigItems.add(new DataConfigItem(false, "Fitting Type"));
        dataConfigItems.add(new DataConfigItem(false, "Chart Line Display Selection"));
        dataConfigItems.add(new DataConfigItem(true, "Chart Control Preferences"));
        dataConfigItems.add(new DataConfigItem(false, "Set Move Step Size"));
        dataConfigItems.add(new DataConfigItem(false, "Set Rotate Angle Size"));
        dataConfigItems.add(new DataConfigItem(false, "Set Rotate Center Coordinate"));
        dataConfigItems.add(new DataConfigItem(false, "Set Scale Step Size"));
        dataConfigItems.add(new DataConfigItem(true, "Chart Display Preferences"));
        dataConfigItems.add(new DataConfigItem(false, "Chart Axes Position"));
        dataConfigItems.add(new DataConfigItem(false, "Chart Dots Display Preferences"));
        dataConfigItems.add(new DataConfigItem(false, "Chart Line Display Preferences"));
        dataConfigItems.add(new DataConfigItem(false, "Chart Dots/Line Custom Color"));
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
        view.findViewById(R.id.result_data_dialog_filter_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterOptionDialog.dismiss();
            }
        });
        view.findViewById(R.id.result_data_dialog_filter_median).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProperties.setFilterMode("median");
                Toast.makeText(DataActivity.this, "Median Filter Applied", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        filterOptionDialog.show();
    }

    private void showFittingOptionDialog() {
        final Dialog fittingOptionDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        fittingOptionDialog.setCancelable(true);
        fittingOptionDialog.setCanceledOnTouchOutside(true);
        Window window = fittingOptionDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_fittingtype, null);
        view.findViewById(R.id.result_data_dialog_fitting_lsm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DataActivity.this, "Least Sqaures Method Applied", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        view.findViewById(R.id.result_data_dialog_fitting_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fittingOptionDialog.dismiss();
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        fittingOptionDialog.show();
    }

    private void showChartLineDisplaySelectionDialog() {
        final Dialog chartLineDisplaySelectionDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        chartLineDisplaySelectionDialog.setCanceledOnTouchOutside(true);
        chartLineDisplaySelectionDialog.setCancelable(true);
        Window window = chartLineDisplaySelectionDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_chartlinedisplayselection, null);
        CheckBox originalCheckBox = view.findViewById(R.id.result_data_dialog_selection_originalCheck);
        CheckBox filteredCheckBox = view.findViewById(R.id.result_data_dialog_selection_filteredCheck);
        CheckBox standardCheckBox = view.findViewById(R.id.result_data_dialog_selection_standardCheck);
        CheckBox fittingCheckBox = view.findViewById(R.id.result_data_dialog_selection_fittingCheck);
        originalCheckBox.setChecked(lineChartProperties.getLineHasOriginalLine());
        filteredCheckBox.setChecked(lineChartProperties.getLineHasFilteredLine());
        standardCheckBox.setChecked(lineChartProperties.getLineHasStandardLine());
        fittingCheckBox.setChecked(lineChartProperties.getLineHasFittingLine());
        originalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lineChartProperties.setLineHasOriginalLine(isChecked);
                initLineChart();
            }
        });
        filteredCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lineChartProperties.setLineHasFilteredLine(isChecked);
                initLineChart();
            }
        });
        standardCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lineChartProperties.setLineHasStandardLine(isChecked);
                initLineChart();
            }
        });
        fittingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lineChartProperties.setLineHasFittingLine(isChecked);
                initLineChart();
            }
        });
        view.findViewById(R.id.result_data_dialog_selection_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartLineDisplaySelectionDialog.dismiss();
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        chartLineDisplaySelectionDialog.show();
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
        final NiceSpinner niceSpinner = view.findViewById(R.id.result_data_dialog_dotprefer_spinner);
        final List<String> spinnerItem = new ArrayList<>();
        spinnerItem.add(getResources().getString(R.string.OriginalDotsOnly));
        spinnerItem.add(getResources().getString(R.string.FilteredDotsOnly));
        spinnerItem.add("Tap to Select an Item");
        niceSpinner.attachDataSource(spinnerItem);
        final Button shapeCircle = view.findViewById(R.id.result_data_dialog_dotshape_circle);
        final Button shapeSqure = view.findViewById(R.id.result_data_dialog_dotshape_square);
        final Button shapeDiamond = view.findViewById(R.id.result_data_dialog_dotshape_diamond);
        final Button dotSize1 = view.findViewById(R.id.result_data_dialog_dotsize_1);
        final Button dotSize2 = view.findViewById(R.id.result_data_dialog_dotsize_2);
        final Button dotSize3 = view.findViewById(R.id.result_data_dialog_dotsize_3);
        final Button dotSize4 = view.findViewById(R.id.result_data_dialog_dotsize_4);
        final Button dotSize5 = view.findViewById(R.id.result_data_dialog_dotsize_5);
        final Switch dotLines = view.findViewById(R.id.result_data_dialog_dotprefer_dotlines);
        final Switch lineFilled = view.findViewById(R.id.result_data_dialog_dotprefer_linefilled);
        final TextView textView = view.findViewById(R.id.result_data_dialog_dotprefer_linefilledtext);
        final TextView textView1 = view.findViewById(R.id.result_data_dialog_dotprefer_dotlinestext);
        // Disable switches when first opened
        dotLines.setEnabled(false);
        lineFilled.setEnabled(false);
        textView.setTextColor(getResources().getColor(R.color.colorDarkGray));
        textView1.setTextColor(getResources().getColor(R.color.colorDarkGray));
        // The same problem again, nicespinner shows the first item but does not change view
        niceSpinner.setSelectedIndex(2);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (spinnerItem.size()==3) {
                    spinnerItem.remove(2);
                    niceSpinner.attachDataSource(spinnerItem);
                }
                if (position != 2) {
                    dotLines.setEnabled(true);
                    lineFilled.setEnabled(true);
                    textView1.setTextColor(getResources().getColor(R.color.colorBlack));
                    switch (lineChartProperties.getLineChartPointShape(position)) {
                        case "circle":
                            shapeCircle.setBackgroundResource(R.drawable.button_left_selected);
                            shapeCircle.setTextColor(getResources().getColor(R.color.colorWhite));
                            shapeSqure.setBackgroundResource(R.drawable.button_center_unselected);
                            shapeSqure.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                            shapeDiamond.setBackgroundResource(R.drawable.button_right_unselected);
                            shapeDiamond.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                            break;
                        case "square":
                            shapeSqure.setBackgroundResource(R.drawable.button_center_selected);
                            shapeSqure.setTextColor(getResources().getColor(R.color.colorWhite));
                            shapeCircle.setBackgroundResource(R.drawable.button_left_unselected);
                            shapeCircle.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                            shapeDiamond.setBackgroundResource(R.drawable.button_right_unselected);
                            shapeDiamond.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                            break;
                        case "diamond":
                            shapeDiamond.setBackgroundResource(R.drawable.button_right_selected);
                            shapeDiamond.setTextColor(getResources().getColor(R.color.colorWhite));
                            shapeCircle.setBackgroundResource(R.drawable.button_left_unselected);
                            shapeCircle.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                            shapeSqure.setBackgroundResource(R.drawable.button_center_unselected);
                            shapeSqure.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                            break;
                    }
                    shapeCircle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!lineChartProperties.getLineChartPointShape(position).equals("circle")) {
                                shapeCircle.setBackgroundResource(R.drawable.button_left_selected);
                                shapeCircle.setTextColor(getResources().getColor(R.color.colorWhite));
                                shapeSqure.setBackgroundResource(R.drawable.button_center_unselected);
                                shapeSqure.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                                shapeDiamond.setBackgroundResource(R.drawable.button_right_unselected);
                                shapeDiamond.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                                lineChartProperties.setLineChartDotShape("circle", position);
                                initLineChart();
                            }
                        }
                    });
                    shapeSqure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!lineChartProperties.getLineChartPointShape(position).equals("square")) {
                                shapeCircle.setBackgroundResource(R.drawable.button_left_unselected);
                                shapeCircle.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                                shapeSqure.setBackgroundResource(R.drawable.button_center_selected);
                                shapeSqure.setTextColor(getResources().getColor(R.color.colorWhite));
                                shapeDiamond.setBackgroundResource(R.drawable.button_right_unselected);
                                shapeDiamond.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                                lineChartProperties.setLineChartDotShape("square", position);
                                initLineChart();
                            }
                        }
                    });
                    shapeDiamond.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!lineChartProperties.getLineChartPointShape(position).equals("diamond")) {
                                shapeCircle.setBackgroundResource(R.drawable.button_left_unselected);
                                shapeCircle.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                                shapeSqure.setBackgroundResource(R.drawable.button_center_unselected);
                                shapeSqure.setTextColor(getResources().getColor(R.color.colorBrightBlue));
                                shapeDiamond.setBackgroundResource(R.drawable.button_right_selected);
                                shapeDiamond.setTextColor(getResources().getColor(R.color.colorWhite));
                                lineChartProperties.setLineChartDotShape("diamond", position);
                                initLineChart();
                            }
                        }
                    });
                    final Button[] dotSizeButtons = new Button[]{dotSize1, dotSize2, dotSize3, dotSize4, dotSize5};
                    clearAllSizeButton(dotSizeButtons);
                    dotSizeButtons[lineChartProperties.getLineChartDotSize(position) - 1].setBackgroundResource(
                            (lineChartProperties.getLineChartDotSize(position) == 1) ? R.drawable.button_left_selected :
                                    ((lineChartProperties.getLineChartDotSize(position) == 5) ? R.drawable.button_right_selected : R.drawable.button_center_selected)
                    );
                    dotSizeButtons[lineChartProperties.getLineChartDotSize(position) - 1].setTextColor(getResources().getColor(R.color.colorWhite));
                    dotSize1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartDotSize(position) != 1) {
                                lineChartProperties.setLineChartDotSize(1, position);
                                setHighlightSizeButton(position, dotSizeButtons, true);
                                initLineChart();
                            }
                        }
                    });
                    dotSize2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartDotSize(position) != 2) {
                                lineChartProperties.setLineChartDotSize(2, position);
                                setHighlightSizeButton(position, dotSizeButtons, true);
                                initLineChart();
                            }
                        }
                    });
                    dotSize3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartDotSize(position) != 3) {
                                lineChartProperties.setLineChartDotSize(3, position);
                                setHighlightSizeButton(position, dotSizeButtons, true);
                                initLineChart();
                            }
                        }
                    });
                    dotSize4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartDotSize(position) != 4) {
                                lineChartProperties.setLineChartDotSize(4, position);
                                setHighlightSizeButton(position, dotSizeButtons, true);
                                initLineChart();
                            }
                        }
                    });
                    dotSize5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartDotSize(position) != 5) {
                                lineChartProperties.setLineChartDotSize(5, position);
                                setHighlightSizeButton(position, dotSizeButtons, true);
                                initLineChart();
                            }
                        }
                    });
                    dotLines.setChecked(lineChartProperties.getLineChartDotHasLines(position));
                    dotLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            lineChartProperties.setLineChartDotHasLines(isChecked, position);
                            initLineChart();
                            if (isChecked) {
                                lineFilled.setEnabled(true);
                                textView.setTextColor(getResources().getColor(R.color.colorBlack));
                                lineFilled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        lineChartProperties.setLineChartDotHasFilled(isChecked, position);
                                        initLineChart();
                                    }
                                });
                            }
                            else {
                                lineFilled.setEnabled(false);
                                textView.setTextColor(getResources().getColor(R.color.colorDarkGray));
                            }
                        }
                    });
                }
                else {
                    shapeCircle.setBackgroundResource(R.drawable.button_left_unselected);
                    shapeSqure.setBackgroundResource(R.drawable.button_center_unselected);
                    shapeDiamond.setBackgroundResource(R.drawable.button_right_unselected);
                    dotSize1.setBackgroundResource(R.drawable.button_left_unselected);
                    dotSize2.setBackgroundResource(R.drawable.button_center_unselected);
                    dotSize3.setBackgroundResource(R.drawable.button_center_unselected);
                    dotSize4.setBackgroundResource(R.drawable.button_center_unselected);
                    dotSize5.setBackgroundResource(R.drawable.button_right_unselected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        view.findViewById(R.id.result_data_dialog_dotprefer_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dotPreferenceChangeDialog.dismiss();
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dotPreferenceChangeDialog.show();
    }

    private void showLinePreferenceChangeDialog() {
        final Dialog linePreferenceChangeDialog = new Dialog(DataActivity.this, R.style.bottomDialog);
        linePreferenceChangeDialog.setCancelable(true);
        linePreferenceChangeDialog.setCanceledOnTouchOutside(true);
        Window window = linePreferenceChangeDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        View view = View.inflate(DataActivity.this, R.layout.result_data_dialog_lineprefer, null);
        window.setContentView(view);
        final NiceSpinner niceSpinner = view.findViewById(R.id.result_data_dialog_lineprefer_spinner);
        final List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add(getResources().getString(R.string.StandardLineOnly));
        spinnerItems.add(getResources().getString(R.string.FittingLineOnly));
        spinnerItems.add("Tap to Select an Item");
        niceSpinner.attachDataSource(spinnerItems);
        niceSpinner.setSelectedIndex(2);
        final Button lineSize1 = view.findViewById(R.id.result_data_dialog_linesize_1);
        final Button lineSize2 = view.findViewById(R.id.result_data_dialog_linesize_2);
        final Button lineSize3 = view.findViewById(R.id.result_data_dialog_linesize_3);
        final Button lineSize4 = view.findViewById(R.id.result_data_dialog_linesize_4);
        final Button lineSize5 = view.findViewById(R.id.result_data_dialog_linesize_5);
        final Switch lineFilled = view.findViewById(R.id.result_data_dialog_lineprefer_linefilled);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (spinnerItems.size()==3) {
                    spinnerItems.remove(2);
                    niceSpinner.attachDataSource(spinnerItems);
                }
                if (position != 2) {
                    final Button[] lineSizeButtons = new Button[]{lineSize1, lineSize2, lineSize3, lineSize4, lineSize5};
                    clearAllSizeButton(lineSizeButtons);
                    lineSizeButtons[lineChartProperties.getLineChartLineSize(position) - 1].setBackgroundResource(
                            (lineChartProperties.getLineChartLineSize(position) == 1) ? R.drawable.button_left_selected :
                                    ((lineChartProperties.getLineChartLineSize(position) == 5) ? R.drawable.button_right_selected : R.drawable.button_center_selected)
                    );
                    lineSizeButtons[lineChartProperties.getLineChartLineSize(position) - 1].setTextColor(getResources().getColor(R.color.colorWhite));
                    lineSize1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartLineSize(position) != 1) {
                                lineChartProperties.setLineChartLineSize(1, position);
                                setHighlightSizeButton(position, lineSizeButtons, false);
                                initLineChart();
                            }
                        }
                    });
                    lineSize2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartLineSize(position) != 2) {
                                lineChartProperties.setLineChartLineSize(2, position);
                                setHighlightSizeButton(position, lineSizeButtons, false);
                                initLineChart();
                            }
                        }
                    });
                    lineSize3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartLineSize(position) != 3) {
                                lineChartProperties.setLineChartLineSize(3, position);
                                setHighlightSizeButton(position, lineSizeButtons, false);
                                initLineChart();
                            }
                        }
                    });
                    lineSize4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartLineSize(position) != 4) {
                                lineChartProperties.setLineChartLineSize(4, position);
                                setHighlightSizeButton(position, lineSizeButtons, false);
                                initLineChart();
                            }
                        }
                    });
                    lineSize5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lineChartProperties.getLineChartLineSize(position) != 5) {
                                lineChartProperties.setLineChartLineSize(5, position);
                                setHighlightSizeButton(position, lineSizeButtons, false);
                                initLineChart();
                            }
                        }
                    });
                    lineFilled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            lineChartProperties.setLineChartLineHasFilled(isChecked, position);
                            initLineChart();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        view.findViewById(R.id.result_data_dialog_lineprefer_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linePreferenceChangeDialog.dismiss();
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        linePreferenceChangeDialog.show();
    }

    private void setHighlightSizeButton(int position, Button[] dotSizeButtons, boolean isDot) {
        int highlightPosition = isDot?(lineChartProperties.getLineChartDotSize(position)-1)
                :(lineChartProperties.getLineChartLineSize(position)-1);
        for (int i = 0; i <= 4; i++) {
            if (i==highlightPosition) {
                switch (i) {
                    case 0:
                        dotSizeButtons[i].setBackgroundResource(R.drawable.button_left_selected);
                        dotSizeButtons[i].setTextColor(getResources().getColor(R.color.colorWhite));
                        break;
                    case 4:
                        dotSizeButtons[i].setBackgroundResource(R.drawable.button_right_selected);
                        dotSizeButtons[i].setTextColor(getResources().getColor(R.color.colorWhite));
                        break;
                    default:
                        dotSizeButtons[i].setBackgroundResource(R.drawable.button_center_selected);
                        dotSizeButtons[i].setTextColor(getResources().getColor(R.color.colorWhite));
                        break;
                }
            }
            else {
                switch (i) {
                    case 0:
                        dotSizeButtons[i].setBackgroundResource(R.drawable.button_left_unselected);
                        dotSizeButtons[i].setTextColor(getResources().getColor(R.color.colorBrightBlue));
                        break;
                    case 4:
                        dotSizeButtons[i].setBackgroundResource(R.drawable.button_right_unselected);
                        dotSizeButtons[i].setTextColor(getResources().getColor(R.color.colorBrightBlue));
                        break;
                    default:
                        dotSizeButtons[i].setBackgroundResource(R.drawable.button_center_unselected);
                        dotSizeButtons[i].setTextColor(getResources().getColor(R.color.colorBrightBlue));
                        break;
                }
            }
        }
    }

    private void clearAllSizeButton(@NonNull Button[] buttons) {
        for (int i = 0; i <= 4; i++) {
            switch (i) {
                case 0:
                    buttons[i].setBackgroundResource(R.drawable.button_left_unselected);
                    break;
                case 4:
                    buttons[i].setBackgroundResource(R.drawable.button_right_unselected);
                    break;
                    default:
                        buttons[i].setBackgroundResource(R.drawable.button_center_unselected);
                        break;
            }
            buttons[i].setTextColor(getResources().getColor(R.color.colorBrightBlue));
        }
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
        final NiceSpinner lineSelector = view.findViewById(R.id.lineSelector);
        final List<String> lineSelectorContent = new ArrayList<>();
        lineSelectorContent.add(getResources().getString(R.string.originalDotsColor));
        lineSelectorContent.add(getResources().getString(R.string.filteredDotsColor));
        lineSelectorContent.add(getResources().getString(R.string.standardDotsColor));
        lineSelectorContent.add(getResources().getString(R.string.fittingDotsColor));
        lineSelector.attachDataSource(lineSelectorContent);
        ImageView showColorBall = view.findViewById(R.id.result_data_dialog_dotcolor_show);
        final GradientDrawable showColorBallBackground = (GradientDrawable) showColorBall.getBackground();
        final SeekBar alphaBar = view.findViewById(R.id.data_data_dialog_dotcolor_alphabar);
        final SeekBar redBar = view.findViewById(R.id.data_data_dialog_dotcolor_redbar);
        final SeekBar greenBar = view.findViewById(R.id.data_data_dialog_dotcolor_greenbar);
        final SeekBar blueBar = view.findViewById(R.id.data_data_dialog_dotcolor_bluebar);
        final TextView alphaText = view.findViewById(R.id.data_data_dialog_dotcolor_alphatext);
        final TextView redText = view.findViewById(R.id.data_data_dialog_dotcolor_redtext);
        final TextView greenText = view.findViewById(R.id.data_data_dialog_dotcolor_greentext);
        final TextView blueText = view.findViewById(R.id.data_data_dialog_dotcolor_bluetext);
        final int decimalOriginalAlpha = Integer.parseInt(lineChartProperties.getLineOriginalPointColorARGB().substring(0, 2), 16);
        final int decimalOriginalRed = Integer.parseInt(lineChartProperties.getLineOriginalPointColorARGB().substring(2, 4), 16);
        final int decimalOriginalGreen = Integer.parseInt(lineChartProperties.getLineOriginalPointColorARGB().substring(4, 6), 16);
        final int decimalOriginalBlue = Integer.parseInt(lineChartProperties.getLineOriginalPointColorARGB().substring(6, 8), 16);
        final int decimalFilteredAlpha = Integer.parseInt(lineChartProperties.getLineFilteredPointColorARGB().substring(0, 2), 16);
        final int decimalFilteredRed = Integer.parseInt(lineChartProperties.getLineFilteredPointColorARGB().substring(2, 4), 16);
        final int decimalFilteredGreen = Integer.parseInt(lineChartProperties.getLineFilteredPointColorARGB().substring(4, 6), 16);
        final int decimalFilteredBlue = Integer.parseInt(lineChartProperties.getLineFilteredPointColorARGB().substring(6, 8), 16);
        final int decimalStandardAlpha = Integer.parseInt(lineChartProperties.getLineStandardPointColorARGB().substring(0, 2), 16);
        final int decimalStandardRed = Integer.parseInt(lineChartProperties.getLineStandardPointColorARGB().substring(2, 4), 16);
        final int decimalStandardGreen = Integer.parseInt(lineChartProperties.getLineStandardPointColorARGB().substring(4, 6), 16);
        final int decimalStandardBlue = Integer.parseInt(lineChartProperties.getLineStandardPointColorARGB().substring(6, 8), 16);
        final int decimalFittingAlpha = Integer.parseInt(lineChartProperties.getLineFittingPointColorARGB().substring(0, 2), 16);
        final int decimalFittingRed = Integer.parseInt(lineChartProperties.getLineFittingPointColorARGB().substring(2, 4), 16);
        final int decimalFittingGreen = Integer.parseInt(lineChartProperties.getLineFittingPointColorARGB().substring(4, 6), 16);
        final int decimalFittingBlue = Integer.parseInt(lineChartProperties.getLineFittingPointColorARGB().substring(6, 8), 16);
        // Probably initiation should be in "onNothingSelected", but it does not work
        alphaBar.setProgress(decimalOriginalAlpha);
        alphaText.setText(String.valueOf(decimalOriginalAlpha));
        redBar.setProgress(decimalOriginalRed);
        redText.setText(String.valueOf(decimalOriginalRed));
        greenBar.setProgress(decimalOriginalGreen);
        greenText.setText(String.valueOf(decimalOriginalGreen));
        blueBar.setProgress(decimalOriginalBlue);
        blueText.setText(String.valueOf(decimalOriginalBlue));
        showColorBallBackground.setColor(Color.parseColor("#"+lineChartProperties.getLineOriginalPointColorARGB()));
        lineSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        alphaBar.setProgress(decimalOriginalAlpha);
                        alphaText.setText(String.valueOf(decimalOriginalAlpha));
                        redBar.setProgress(decimalOriginalRed);
                        redText.setText(String.valueOf(decimalOriginalRed));
                        greenBar.setProgress(decimalOriginalGreen);
                        greenText.setText(String.valueOf(decimalOriginalGreen));
                        blueBar.setProgress(decimalOriginalBlue);
                        blueText.setText(String.valueOf(decimalOriginalBlue));
                        showColorBallBackground.setColor(Color.parseColor("#"+lineChartProperties.getLineOriginalPointColorARGB()));
                        break;
                    case 1:
                        alphaBar.setProgress(decimalFilteredAlpha);
                        alphaText.setText(String.valueOf(decimalFilteredAlpha));
                        redBar.setProgress(decimalFilteredRed);
                        redText.setText(String.valueOf(decimalFilteredRed));
                        greenBar.setProgress(decimalFilteredGreen);
                        greenText.setText(String.valueOf(decimalFilteredGreen));
                        blueBar.setProgress(decimalFilteredBlue);
                        blueText.setText(String.valueOf(decimalFilteredBlue));
                        showColorBallBackground.setColor(Color.parseColor("#"+lineChartProperties.getLineFilteredPointColorARGB()));
                        break;
                    case 2:
                        alphaBar.setProgress(decimalStandardAlpha);
                        alphaText.setText(String.valueOf(decimalStandardAlpha));
                        redBar.setProgress(decimalStandardRed);
                        redText.setText(String.valueOf(decimalStandardRed));
                        greenBar.setProgress(decimalStandardGreen);
                        greenText.setText(String.valueOf(decimalStandardGreen));
                        blueBar.setProgress(decimalStandardBlue);
                        blueText.setText(String.valueOf(decimalStandardBlue));
                        showColorBallBackground.setColor(Color.parseColor("#"+lineChartProperties.getLineStandardPointColorARGB()));
                        break;
                    case 3:
                        alphaBar.setProgress(decimalFittingAlpha);
                        alphaText.setText(String.valueOf(decimalFittingAlpha));
                        redBar.setProgress(decimalFittingRed);
                        redText.setText(String.valueOf(decimalFittingRed));
                        greenBar.setProgress(decimalFittingGreen);
                        greenText.setText(String.valueOf(decimalFittingGreen));
                        blueBar.setProgress(decimalFittingBlue);
                        blueText.setText(String.valueOf(decimalFittingBlue));
                        showColorBallBackground.setColor(Color.parseColor("#"+lineChartProperties.getLineFittingPointColorARGB()));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        alphaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alphaText.setText(String.valueOf(progress));
                alphaText.setTextColor(Color.parseColor("#"
                        +(Integer.toHexString(progress).length()==1?"0"+Integer.toHexString(progress):Integer.toHexString(progress))+"000000"));
                String colorBallColorString =
                        (Integer.toHexString(alphaBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(alphaBar.getProgress()):Integer.toHexString(alphaBar.getProgress()))+
                        (Integer.toHexString(redBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(redBar.getProgress()):Integer.toHexString(redBar.getProgress()))+
                        (Integer.toHexString(greenBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(greenBar.getProgress()):Integer.toHexString(greenBar.getProgress()))+
                        (Integer.toHexString(blueBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(blueBar.getProgress()):Integer.toHexString(blueBar.getProgress()));
                showColorBallBackground.setColor(Color.parseColor("#"+colorBallColorString));
                switch (lineSelector.getSelectedIndex()) {
                    case 0:
                        lineChartProperties.setLineOriginalPointColorARGB(colorBallColorString);
                        break;
                    case 1:
                        lineChartProperties.setLineFilteredPointColorARGB(colorBallColorString);
                        break;
                    case 2:
                        lineChartProperties.setLineStandardPointColorARGB(colorBallColorString);
                        break;
                    case 3:
                        lineChartProperties.setLineFittingPointColorARGB(colorBallColorString);
                        break;
                }
                initLineChart();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        redBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                redText.setText(String.valueOf(progress));
                redText.setTextColor(Color.parseColor("#"+"FF"
                        +(Integer.toHexString(progress).length()==1?"0"+Integer.toHexString(progress):Integer.toHexString(progress))+"0000"));
                String colorBallColorString =
                        (Integer.toHexString(alphaBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(alphaBar.getProgress()):Integer.toHexString(alphaBar.getProgress()))+
                        (Integer.toHexString(redBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(redBar.getProgress()):Integer.toHexString(redBar.getProgress()))+
                        (Integer.toHexString(greenBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(greenBar.getProgress()):Integer.toHexString(greenBar.getProgress()))+
                        (Integer.toHexString(blueBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(blueBar.getProgress()):Integer.toHexString(blueBar.getProgress()));
                showColorBallBackground.setColor(Color.parseColor("#"+colorBallColorString));
                switch (lineSelector.getSelectedIndex()) {
                    case 0:
                        lineChartProperties.setLineOriginalPointColorARGB(colorBallColorString);
                        break;
                    case 1:
                        lineChartProperties.setLineFilteredPointColorARGB(colorBallColorString);
                        break;
                    case 2:
                        lineChartProperties.setLineStandardPointColorARGB(colorBallColorString);
                        break;
                    case 3:
                        lineChartProperties.setLineFittingPointColorARGB(colorBallColorString);
                        break;
                }
                initLineChart();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        greenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                greenText.setText(String.valueOf(progress));
                greenText.setTextColor(Color.parseColor("#"+"FF00"
                        +(Integer.toHexString(progress).length()==1?"0"+Integer.toHexString(progress):Integer.toHexString(progress))+"00"));
                String colorBallColorString =
                        (Integer.toHexString(alphaBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(alphaBar.getProgress()):Integer.toHexString(alphaBar.getProgress()))+
                        (Integer.toHexString(redBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(redBar.getProgress()):Integer.toHexString(redBar.getProgress()))+
                        (Integer.toHexString(greenBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(greenBar.getProgress()):Integer.toHexString(greenBar.getProgress()))+
                        (Integer.toHexString(blueBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(blueBar.getProgress()):Integer.toHexString(blueBar.getProgress()));
                showColorBallBackground.setColor(Color.parseColor("#"+colorBallColorString));
                switch (lineSelector.getSelectedIndex()) {
                    case 0:
                        lineChartProperties.setLineOriginalPointColorARGB(colorBallColorString);
                        break;
                    case 1:
                        lineChartProperties.setLineFilteredPointColorARGB(colorBallColorString);
                        break;
                    case 2:
                        lineChartProperties.setLineStandardPointColorARGB(colorBallColorString);
                        break;
                    case 3:
                        lineChartProperties.setLineFittingPointColorARGB(colorBallColorString);
                        break;
                }
                initLineChart();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        blueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blueText.setText(String.valueOf(progress));
                blueText.setTextColor(Color.parseColor("#"+"FF0000"+
                        (Integer.toHexString(progress).length()==1?"0"+Integer.toHexString(progress):Integer.toHexString(progress))));
                String colorBallColorString =
                        (Integer.toHexString(alphaBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(alphaBar.getProgress()):Integer.toHexString(alphaBar.getProgress()))+
                        (Integer.toHexString(redBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(redBar.getProgress()):Integer.toHexString(redBar.getProgress()))+
                        (Integer.toHexString(greenBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(greenBar.getProgress()):Integer.toHexString(greenBar.getProgress()))+
                        (Integer.toHexString(blueBar.getProgress()).length()==1?
                                "0"+Integer.toHexString(blueBar.getProgress()):Integer.toHexString(blueBar.getProgress()));
                showColorBallBackground.setColor(Color.parseColor("#"+colorBallColorString));
                switch (lineSelector.getSelectedIndex()) {
                    case 0:
                        lineChartProperties.setLineOriginalPointColorARGB(colorBallColorString);
                        break;
                    case 1:
                        lineChartProperties.setLineFilteredPointColorARGB(colorBallColorString);
                        break;
                    case 2:
                        lineChartProperties.setLineStandardPointColorARGB(colorBallColorString);
                        break;
                    case 3:
                        lineChartProperties.setLineFittingPointColorARGB(colorBallColorString);
                        break;
                }
                initLineChart();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        view.findViewById(R.id.result_data_dialog_dotcolor_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorCustomDialog.dismiss();
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        colorCustomDialog.show();
    }

    private void showSavingDataDialog() {

    }

    private boolean isNullEmptyBlank(@NonNull String str){
        return str == null || "".equals(str) || "".equals(str.trim());
    }
}
