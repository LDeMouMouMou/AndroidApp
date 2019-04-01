package com.example.measureit.MyClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ConfigurationSaver {

    //
    private Context context;
    private String saverName;
    // private String[] typeArray = new String[]{"Round", "Ellipicity", "Mixed", "Unknown"};

    //
    public void configurationSaverInit(Context inputContext, boolean isExist, String existSaverName){
        context = inputContext;
        if (isExist){
            saverName = existSaverName;
        }
    }

    public String[] getConfigurationNameList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("configurationList2", Context.MODE_PRIVATE);
        Map<String, ?> sharedPreferencesAll = sharedPreferences.getAll();
        List<String> allSaverName = new ArrayList<>();
        for (int i = 0; i < sharedPreferencesAll.size(); i++){
            if (!isNullEmptyBlank(sharedPreferences.getString("saverName" + i, ""))) {
                allSaverName.add(sharedPreferences.getString("saverName" + i, ""));
            }
        }
        String[] names = new String[allSaverName.size()];
        allSaverName.toArray(names);
        return names;
    }

    public String[] getConfigurationTimeList(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("configurationList2", Context.MODE_PRIVATE);
        // SharedPreferences.Editor editor = sharedPreferences.edit();
        Map<String, ?> sharedPreferencesAll = sharedPreferences.getAll();
        List<String> allSaverTime = new ArrayList<>();
        for (int i = 0; i < sharedPreferencesAll.size(); i++){
            if (!isNullEmptyBlank(sharedPreferences.getString("saverTime" + i, ""))) {
                allSaverTime.add(sharedPreferences.getString("saverTime" + i, ""));
            }
        }
        String[] times = new String[allSaverTime.size()];
        allSaverTime.toArray(times);
        // editor.apply();
        return times;
    }

    // Based on created time is not a good idea, using modified time and negetive sequence is better


    // Create a new configuration for button "add a new"
    public void addNewSaver(String newSaverName){
        saverName = newSaverName;
        SharedPreferences sharedPreferences = context.getSharedPreferences(saverName, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = context.getSharedPreferences("configurationList2", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor.putString("configurationName", saverName);
        editor2.putString("saverName"+(sharedPreferences2.getAll().size()), saverName);
        Calendar calendar = Calendar.getInstance();
        String time = DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime()).toString();
        editor.putString("createdTime", time);
        editor2.putString("saverTime"+(sharedPreferences2.getAll().size()), time);
        editor.apply();
        editor2.apply();
    }

    // Remove a configuration
    public void delSaver(String selectedSaverName){
        // Clear the configuration contents
        SharedPreferences sharedPreferences = context.getSharedPreferences(selectedSaverName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        // Clear the names&times list
        // Make the selected position's value = ""
        // Then it will be cleared when traversing to output list
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("configurationList2", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        Map<String, ?> sharedPreferences1All = sharedPreferences1.getAll();
        for (int i = 0; i < sharedPreferences1All.size(); i++)
        {
            if (sharedPreferences1.getString("saverName" + i, "").equals(selectedSaverName))
            {
                editor1.putString("saverName" + i, "");
                editor1.putString("saverTime" + i, "");
                editor1.apply();
                break;
            }
        }
    }

    // Now the configuration is created, and the saverName is specified
    public void addParamsUnrandom(int typeNum, boolean nonStdHead, float concave, float convex,
                                  float headRadius, float curvedSurface, float headTotal,
                                  boolean ellipicityDetection, float padHeight, int pointsProgress, int angleProgress) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(saverName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("typeNum", typeNum);
        editor.putBoolean("nonStdHead", nonStdHead);
        if (nonStdHead) {
            editor.putFloat("concave", concave);
            editor.putFloat("convex", convex);
        }
        editor.putFloat("headRadius", headRadius);
        editor.putFloat("curvedSurface", curvedSurface);
        editor.putFloat("headTotal", headTotal);
        editor.putBoolean("ellipicityDetection", ellipicityDetection);
        editor.putFloat("padHeight", padHeight);
        editor.putBoolean("randomData", false);
        editor.putInt("pointsProgress", pointsProgress);
        editor.putInt("angleProgress", angleProgress);
        editor.apply();
        updateTime();
    }

    public void addParamsRandom(float stdRadius, float minRadius, float maxRadius, int pointsProgress, int angleProgress) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(saverName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("stdRadius", stdRadius);
        editor.putFloat("minRadius", minRadius);
        editor.putFloat("maxRadius", maxRadius);
        editor.putBoolean("randomData", true);
        editor.putInt("pointsProgress", pointsProgress);
        editor.putInt("angleProgress", angleProgress);
        editor.apply();
    }

    private void updateTime(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(saverName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Calendar calendar = Calendar.getInstance();
        String time = DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime()).toString();
        editor.putString("modifiedTime", time);
        editor.apply();
    }

    // Get the Float Parameters
    public float getFloatParams(String type){
        SharedPreferences sharedPreferences = context.getSharedPreferences(saverName, Context.MODE_PRIVATE);
        switch (type){
            case "concave":
                return sharedPreferences.getFloat("concave", 0);
            case "convex":
                return sharedPreferences.getFloat("convex", 0);
            case "headRadius":
                return sharedPreferences.getFloat("headRadius", 0);
            case "curvedSurface":
                return sharedPreferences.getFloat("curvedSurface", 0);
            case "headTotal":
                return sharedPreferences.getFloat("headTotal", 0);
            case "padHeight":
                return sharedPreferences.getFloat("padHeight", 0);
            case "stdRadius":
                return sharedPreferences.getFloat("stdRadius", 10);
            case "minRadius":
                return sharedPreferences.getFloat("minRadius", 9);
            case "maxRadius":
                return sharedPreferences.getFloat("maxRadius", 11);
        }
        return -1;
    }

    // Get the Boolean Parameters(CheckBoxes)
    public Boolean getBooleanParams(String type){
        SharedPreferences sharedPreferences = context.getSharedPreferences(saverName, Context.MODE_PRIVATE);
        switch (type) {
            case "nonStdHead":
                return sharedPreferences.getBoolean("nonStdHead", false);
            case "ellipicityDetection":
                return sharedPreferences.getBoolean("ellipicityDetection", false);
            case "randomData":
                return sharedPreferences.getBoolean("randomData", false);
        }
        return true;
    }

    // Get the Integer Type
    public int getIntParams(String type){
        SharedPreferences sharedPreferences = context.getSharedPreferences(saverName, Context.MODE_PRIVATE);
        switch (type){
            case "typeNum":
                return sharedPreferences.getInt("typeNum", 0);
            case "pointsProgress":
                return sharedPreferences.getInt("pointsProgress", 1);
            case "angleProgress":
                return sharedPreferences.getInt("angleProgress", 10);
        }
        return 0;
    }

    private boolean isNullEmptyBlank(String str){
        if (str == null || "".equals(str) || "".equals(str.trim())){
            return true;
        }
        return false;
    }

}
