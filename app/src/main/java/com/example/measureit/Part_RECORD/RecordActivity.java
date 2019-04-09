package com.example.measureit.Part_RECORD;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.measureit.MainActivity;
import com.example.measureit.MyClass.DataSaver;
import com.example.measureit.MyClass.ExcelUtil;
import com.example.measureit.Part_NEW.DataSession.DataActivity;
import com.example.measureit.Part_NEW.DataSession.ExcelDataItem;
import com.example.measureit.R;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import gdut.bsx.share2.FileUtil;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;

public class RecordActivity extends AppCompatActivity implements TbsReaderView.ReaderCallback {

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
                bottomDialog.dismiss();
                showExportDialog();
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

    private void showExportDialog() {
        final Dialog exportDialog = new Dialog(RecordActivity.this, R.style.bottomDialog);
        exportDialog.setCancelable(true);
        exportDialog.setCanceledOnTouchOutside(true);
        Window window = exportDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        View view = View.inflate(RecordActivity.this, R.layout.record_export_filetype, null);
        view.findViewById(R.id.record_export_filetype_xsl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDialog.dismiss();
                showXslNameDialog();
            }
        });
        view.findViewById(R.id.record_export_filetype_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.findViewById(R.id.record_export_filetype_jpg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.findViewById(R.id.record_export_filetype_pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.findViewById(R.id.record_export_filetype_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDialog.dismiss();
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        exportDialog.show();
    }

    private void showXslNameDialog() {
        final Dialog xslNameDialog = new Dialog(RecordActivity.this, R.style.centerDialog);
        xslNameDialog.setCancelable(true);
        xslNameDialog.setCanceledOnTouchOutside(true);
        Window window = xslNameDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        View view = View.inflate(RecordActivity.this, R.layout.record_export_xsl_name, null);
        window.setContentView(view);
        final EditText editText = view.findViewById(R.id.record_export_xsl_editname);
        String hintText = "Default: "+dataSaverName+".xsl";
        editText.setHint(hintText);
        view.findViewById(R.id.record_export_xsl_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xslNameDialog.dismiss();
                if (isNullEmptyBlank(editText.getText().toString())) {
                    saveDataToExcel(null);
                }
                else {
                    saveDataToExcel(editText.getText().toString());
                }
            }
        });
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        xslNameDialog.show();
    }

    private void showSuccessDialog(final String filepath, final String filename) {
        final Dialog successDialog = new Dialog(RecordActivity.this, R.style.centerDialog);
        successDialog.setCancelable(false);
        successDialog.setCanceledOnTouchOutside(true);
        Window window = successDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        View view = View.inflate(RecordActivity.this, R.layout.record_export_xsl_success, null);
        TextView filePath = view.findViewById(R.id.record_export_success_filepath);
        String filePathText = "FilePath: "+filepath.substring(0, filepath.lastIndexOf("/"));
        filePath.setText(filePathText);
        TextView fileName = view.findViewById(R.id.record_export_success_filename);
        fileName.setText("FileName: "+filename+".xls");
//        final TbsReaderView tbsReaderView = new TbsReaderView(this, this);
//        RelativeLayout relativeLayout = findViewById(R.id.record_export_success_view);
//        relativeLayout.addView(tbsReaderView, new RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//        view.findViewById(R.id.record_export_success_preview).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("filePath", filepath);
//                bundle.putString("tempPath", Environment.getExternalStorageDirectory().getPath());
//                boolean result = tbsReaderView.preOpen(parseFormat(filename), false);
//                if (result) {
//                    tbsReaderView.openFile(bundle);
//                }
//            }
//        });
        view.findViewById(R.id.record_export_success_systemshare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Share2.Builder(RecordActivity.this)
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(FileUtil.getFileUri(RecordActivity.this, ShareContentType.FILE, new File(filepath)))
                        .build()
                        .shareBySystem();
            }
        });
        view.findViewById(R.id.record_export_success_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog.dismiss();
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        successDialog.show();
    }

    private void saveDataToExcel(String fileName) {
        if (fileName == null) {
            fileName = dataSaverName;
        }
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        // String filePath = getApplicationContext().getFilesDir().getPath();
        String excelFileName = "/"+fileName+".xls";
        String[] title = {"Angle", "Range"};
        String sheetName = "demoSheet";
        List<ExcelDataItem> excelDataItemList = new ArrayList<>();
        ExcelDataItem excelDataItem1 = new ExcelDataItem(1, 1);
        ExcelDataItem excelDataItem2 = new ExcelDataItem(2, 2);
        ExcelDataItem excelDataItem3 = new ExcelDataItem(2, 2);
        excelDataItemList.add(excelDataItem1);
        excelDataItemList.add(excelDataItem2);
        excelDataItemList.add(excelDataItem3);
        filePath = filePath + excelFileName;
        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.initExcel(filePath, sheetName, title);
        excelUtil.writeObjListToExcel(excelDataItemList, filePath, getApplicationContext());
        Toast.makeText(RecordActivity.this, filePath, Toast.LENGTH_SHORT).show();
        showSuccessDialog(filePath, fileName);
//        File file = new File(filePath);
//        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.measureit.fileprovider", file);
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setDataAndType(uri, "application/vnd.ms-excel");
//        startActivityForResult(intent, 1);
    }


    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    private boolean isNullEmptyBlank(@NonNull String str){
        return str == null || "".equals(str) || "".equals(str.trim());
    }

}
