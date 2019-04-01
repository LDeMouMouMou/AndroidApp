package com.example.measureit.Part_NEW.ResultFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.measureit.MyClass.DataSaver;
import com.example.measureit.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class DataFragment extends Fragment {

    private LineChartView lineChartView;
    private List<PointValue> originalDataPointValues = new ArrayList<>();
    private DataSaver dataSaver;
    private String dataSaverName;
    private List<Float> angleList;
    private List<Float> rangeList;
    private List<Float> xValueList;
    private List<Float> yValueList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.resultfragment_data, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle == null) { return; }
        else { dataSaverName = bundle.getString("dataSaverName"); }
        Toast.makeText(getContext(), "This is Data", Toast.LENGTH_SHORT).show();
        lineChartView = getView().findViewById(R.id.dataChart);
        dataSaver = new DataSaver();
        dataSaver.saverInit(getContext(), "output");
        angleList = dataSaver.readAngleData(dataSaverName);
        rangeList = dataSaver.readRangeData(dataSaverName);
        drawOriginalLineChart();
    }

    private void drawOriginalLineChart() {
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

    private void initLineChart() {
        Line line = new Line(originalDataPointValues).setColor(Color.parseColor("#FFCD41"));
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

}
