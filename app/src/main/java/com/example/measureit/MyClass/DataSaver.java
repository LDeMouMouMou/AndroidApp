package com.example.measureit.MyClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSaver {

    private Context context;
    private String dataSaverName;
    private ConfigurationSaver configurationSaver;
    private Map<String, Float> angleMap = new HashMap<>();
    private Map<String, Float> rangeMap = new HashMap<>();

    public void saverInit(Context inputContext, String mode) {
        context = inputContext;
        if (mode.equals("input")) {
            configurationSaver = new ConfigurationSaver();
            if (angleMap.size() != 0) {
                angleMap = new HashMap<>();
            }
            if (rangeMap.size() != 0) {
                rangeMap = new HashMap<>();
            }
        }
    }

    public void addNewDataSet(String configurationName, boolean isRandom) {
        // Add DataSet's Information into file named "allDataSaverInfo"
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("allDataSaverInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        Calendar calendar = Calendar.getInstance();
        String saveTime = DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime()).toString();
        dataSaverName = configurationName+"@"+saveTime;
        // Key for Storing: "DataSaverInfo"+(Current Total File Size) (Not Important for that)
        // Value for Storing: (Configuration Name Used)"@"(Create Time) (Just for Unique)
        editor1.putString("DataSaverInfo"+(sharedPreferences1.getAll().size()+1), dataSaverName);
        // Create a file named (dataSaverName) for storing all parameters and measuring data
        SharedPreferences sharedPreferences2 = context.getSharedPreferences(dataSaverName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        // isRandom is the most important one
        editor2.putBoolean("randomData", isRandom);
        editor1.apply();
        editor2.apply();
        getConfigurationFromSaver(configurationName);
    }

    private void getConfigurationFromSaver(String usedConfigurationName) {
        // Transfer configuration to dataSaver
        configurationSaver.configurationSaverInit(context, true, usedConfigurationName);
        SharedPreferences sharedPreferences = context.getSharedPreferences(dataSaverName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("randomData", false)) {
            editor.putFloat("stdRadius", configurationSaver.getFloatParams("stdRadius"));
            editor.putFloat("minRadius", configurationSaver.getFloatParams("minRadius"));
            editor.putFloat("maxRadius", configurationSaver.getFloatParams("maxRadius"));
        }
        else {
            editor.putFloat("headRadius", configurationSaver.getFloatParams("headRadius"));
            editor.putFloat("curvedSurface", configurationSaver.getFloatParams("curvedSurface"));
            editor.putFloat("headTotal", configurationSaver.getFloatParams("headTotal"));
            editor.putFloat("padHeight", configurationSaver.getFloatParams("padHeight"));
            editor.putBoolean("nonStdHead", configurationSaver.getBooleanParams("nonStdHead"));
            editor.putBoolean("ellipicityDetection", configurationSaver.getBooleanParams("ellipicityDetection"));
            editor.putFloat("concave", configurationSaver.getFloatParams("concave"));
            editor.putFloat("convex", configurationSaver.getFloatParams("convex"));
        }
        editor.putInt("pointsProgress", configurationSaver.getIntParams("pointsProgress"));
        editor.putInt("angleProgress", configurationSaver.getIntParams("angleProgress"));
        editor.apply();
    }

    public void updateData(int position, float angle, float range) {
        angleMap.put("angle"+position, angle);
        rangeMap.put("range"+position, range);
    }

    public boolean writeData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(dataSaverName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Check if the dataSet is valid
        if (angleMap.size() == rangeMap.size()) {
            if (angleMap.size() == sharedPreferences.getInt("angleProgress", 10)) {
                for (String key : angleMap.keySet()) {
                    editor.putFloat(key, angleMap.get(key));
                }
                for (String key : rangeMap.keySet()) {
                    editor.putFloat(key, rangeMap.get(key));
                }
            }
        }
        editor.apply();
        return true;
    }

    public List<Float> readAngleData(String dataSaverName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(dataSaverName, Context.MODE_PRIVATE);
        List<Float> angleList = new ArrayList<>();
        for (int i = 1; i <= sharedPreferences.getInt("angleProgress", 0); i++) {
            angleList.add(sharedPreferences.getFloat("angle"+i, -1));
        }
        return angleList;
    }

    public List<Float> readRangeData(String dataSaverName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(dataSaverName, Context.MODE_PRIVATE);
        List<Float> rangeList = new ArrayList<>();
        for (int i = 1; i <= sharedPreferences.getInt("angleProgress", 0); i++) {
            rangeList.add(sharedPreferences.getFloat("range"+i, -1));
        }
        return rangeList;
    }

    public String getDataSaverName() {
        return dataSaverName;
    }

    public List<String> getAllDataSaverName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("allDataSaverInfo", Context.MODE_PRIVATE);
        Map<String, ?> sharedPreferencesAll = sharedPreferences.getAll();
        List<String> allSaverInfo = new ArrayList<>();
        for (String key : sharedPreferencesAll.keySet()) {
            allSaverInfo.add(sharedPreferences.getString(key, ""));
        }
        return allSaverInfo;
    }

    public boolean getBooleanParams(String type, String saverName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(saverName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(type, false);
    }

    public void delSaver(String saverName) {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("allDataSaverInfo", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = context.getSharedPreferences(saverName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        Map<String, ?> sharedPreferences1All = sharedPreferences1.getAll();
        for (String key : sharedPreferences1All.keySet()) {
            if (sharedPreferences1.getString(key, "").equals(saverName)) {
                editor1.remove(key);
                break;
            }
        }
        editor2.clear();
        editor1.apply();
        editor2.apply();
    }

}
