package com.example.measureit.Part_RECORD;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.measureit.MainActivity;
import com.example.measureit.MyClass.DataSaver;
import com.example.measureit.Part_NEW.ResultActivity;
import com.example.measureit.R;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Dialog bottomDialog;
    private DataSaver dataSaver;

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
                if (view.getId() == R.id.recordMenu) {
                    showBottomDialog(allSaverName.get(position), position);
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
                Intent intent = new Intent(RecordActivity.this, ResultActivity.class);
                intent.putExtra("dataSaverName", saverName);
                intent.putExtra("isBackable", true);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.Pop_Info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecordActivity.this, "Information", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.Pop_Export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecordActivity.this, "Export", Toast.LENGTH_SHORT).show();
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
}
