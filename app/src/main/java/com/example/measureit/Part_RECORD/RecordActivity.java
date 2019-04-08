package com.example.measureit.Part_RECORD;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.measureit.MainActivity;
import com.example.measureit.MyClass.DataSaver;
import com.example.measureit.MyClass.ExcelUtil;
import com.example.measureit.Part_NEW.DataSession.DataActivity;
import com.example.measureit.Part_NEW.DataSession.ExcelDataItem;
import com.example.measureit.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Dialog bottomDialog;
    private DataSaver dataSaver;
    private String dataSaverName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        recyclerView = findViewById(R.id.record_recycler);
        Button backButton = findViewById(R.id.backHomepage);
        dataSaver = new DataSaver();
        dataSaver.saverInit(getApplicationContext(), "output");
        showRecyclerView();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
            }
        });
    }

    private void showRecyclerView() {
        RecyclerModel recyclerModel;
        List<RecyclerModel> models = new ArrayList<>();
        final List<String> allSaverName = dataSaver.getAllDataSaverName();
        for (int i = 0; i < allSaverName.size(); i++) {
            recyclerModel = new RecyclerModel();
            recyclerModel.setRecordMode(dataSaver.getBooleanParams("randomData", allSaverName.get(i))?"Random Data":"Real-Time");
            recyclerModel.setRecordNumber("item "+i);
            recyclerModel.setCreatedTime("Created On: "+allSaverName.get(i).substring(allSaverName.get(i).indexOf("@")+1));
            models.add(recyclerModel);
        }
        MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter(R.layout.record_item, models);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myRecyclerAdapter);
        myRecyclerAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                dataSaverName = allSaverName.get(position);
                if (view.getId() == R.id.recordMenu) {
                    showBottomDialog(dataSaverName, position);
                }
            }
        });
    }

    private void showBottomDialog(final String saverName, final int position) {
        bottomDialog = new Dialog(this, R.style.bottomDialog);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.setCancelable(true);
        Window window = bottomDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        View view = View.inflate(this, R.layout.record_popwindow, null);
        view.findViewById(R.id.Pop_View).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordActivity.this, DataActivity.class);
                intent.putExtra("dataSaverName", dataSaverName);
                intent.putExtra("isBackable", true);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.Pop_Info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecordActivity.this, "Information", Toast.LENGTH_SHORT).show();
                showInfoDialog();
            }
        });
        view.findViewById(R.id.Pop_Export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excelTest();
                // Toast.makeText(RecordActivity.this, "Export", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.Pop_Delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSaver.delSaver(saverName);
                Toast.makeText(RecordActivity.this, "Delete Successfully", Toast.LENGTH_SHORT).show();
                bottomDialog.dismiss();
                showRecyclerView();
            }
        });
        view.findViewById(R.id.Pop_Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomDialog != null && bottomDialog.isShowing()) {
                    bottomDialog.dismiss();
                }
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        bottomDialog.show();
    }

    private void showInfoDialog() {
        Dialog infoDialog = new Dialog(RecordActivity.this, R.style.centerDialog);
        infoDialog.setCancelable(true);
        infoDialog.setCanceledOnTouchOutside(true);
        Window window = infoDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        View view = View.inflate(RecordActivity.this, R.layout.result_data_dialog_info, null);
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

    private void excelTest() {
        String filePath = getApplicationContext().getFilesDir().getPath();
        String excelFileName = "/demo.xls";
        String[] title = {"Angle", "Range"};
        String sheetName = "demoSheet";
        List<ExcelDataItem> excelDataItemList = new ArrayList<>();
        ExcelDataItem excelDataItem1 = new ExcelDataItem(1, 1);
        ExcelDataItem excelDataItem2 = new ExcelDataItem(2, 2);
        excelDataItemList.add(excelDataItem1);
        excelDataItemList.add(excelDataItem2);
        filePath = filePath + excelFileName;
        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.initExcel(filePath, sheetName, title);
        excelUtil.writeObjListToExcel(excelDataItemList, filePath, getApplicationContext());
        Toast.makeText(RecordActivity.this, "Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.inten.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        startActivity(intent);
    }

}
