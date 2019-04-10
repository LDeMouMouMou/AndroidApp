package com.example.measureit.Part_RECORD;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.measureit.R;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

public class PreviewActivity extends AppCompatActivity implements TbsReaderView.ReaderCallback {

    private TbsReaderView tbsReaderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        TextView titleName = findViewById(R.id.previewTitle);
        String filePath = getIntent().getStringExtra("filepath");
        String fileName = getIntent().getStringExtra("filename");
        Button backButton = findViewById(R.id.backHomepage);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreviewActivity.this, RecordActivity.class));
                finish();
            }
        });
        RelativeLayout previewLayout = findViewById(R.id.record_export_preview);
        tbsReaderView = new TbsReaderView(this, this);
        previewLayout.addView(tbsReaderView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        if (filePath == null) {
            return;
        }
        String tbsReaderTemp = Environment.getExternalStorageDirectory() + "/TbsReaderTemp";
        File tbsReaderTempFile = new File(tbsReaderTemp);
        if (!tbsReaderTempFile.exists()) {
            if (!tbsReaderTempFile.mkdir()) {
                Log.d("print", "Create Temp File Failed");
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", tbsReaderTemp);
        boolean result = tbsReaderView.preOpen(getFileType(filePath), false);
        if (result) {
            tbsReaderView.openFile(bundle);
        }
    }

    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            Log.d("print", "paramString---->null");
            return str;
        }
        Log.d("print", "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            Log.d("print", "i <= -1");
            return str;
        }

        str = paramString.substring(i + 1);
        Log.d("print", "paramString.substring(i + 1)------>" + str);
        return str;
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tbsReaderView.onStop();
    }
}
