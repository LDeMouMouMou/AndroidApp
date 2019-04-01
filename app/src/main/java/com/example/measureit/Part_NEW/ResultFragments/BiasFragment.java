package com.example.measureit.Part_NEW.ResultFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.measureit.MyClass.DataCalculation;
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

public class BiasFragment extends Fragment {

    // Defination of Bias LineChart
    public LineChartView lineChartView;
    public List<PointValue> mPointValues = new ArrayList<>();
    //
    public DataCalculation dataCalculation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.resultfragment_bias, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        lineChartView = getView().findViewById(R.id.biasChart);
        dataCalculation = new DataCalculation();
        Toast.makeText(getContext(), "This is Bias", Toast.LENGTH_SHORT).show();
        initBiasData();
        initLineChart();
    }

    private void initBiasData(){
        // Float[] OriginalData = dataCalculation.getListData("bias");
        for (int i = 0; i < dataCalculation.getTotalCount(); i++) {
            mPointValues.add(new PointValue(1, 1));
        }
    }


    private void initLineChart(){
        // mAxisXValues.add(new AxisValue(count).setValue(xval));
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

}
