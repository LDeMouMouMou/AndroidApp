package com.example.measureit;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

public class InitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.e("@@","加载内核是否成功:"+b);
            }
        });
    }
}
