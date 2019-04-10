package com.example.measureit.Part_RECORD;

public class ExcelDataItem {

    private float angle;
    private float originalRange;
    private float x;
    private float y;
    private float bias;

    public ExcelDataItem(float angle, float originalRange, float x, float y, float bias) {
        this.angle = angle;
        this.originalRange = originalRange;
        this.x = x;
        this.y = y;
        this.bias = bias;
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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getBias() {
        return bias;
    }

    public void setBias(float bias) {
        this.bias = bias;
    }
}
