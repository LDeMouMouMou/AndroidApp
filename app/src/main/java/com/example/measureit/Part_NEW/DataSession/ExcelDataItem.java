package com.example.measureit.Part_NEW.DataSession;

public class ExcelDataItem {

    private float angle;
    private float originalRange;

    public ExcelDataItem(float angle, float originalRange) {
        this.angle = angle;
        this.originalRange = originalRange;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getAngle() {
        return angle;
    }

    public void setOriginalRange(float range) {
        this.originalRange = range;
    }

    public float getOriginalRange() {
        return  originalRange;
    }

}
