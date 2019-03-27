package com.example.measureit.MyClass;

import java.util.ArrayList;

public class DataCalculation {

    // Defination of Data ArrayList
    private ArrayList<Float> originalRangeData = new ArrayList<>();
    private ArrayList<Float> originalXData = new ArrayList<>();
    private ArrayList<Float> originalYData = new ArrayList<>();
    private ArrayList<Float> denoisedRangeData = new ArrayList<>();
    private ArrayList<Float> originalBiasData = new ArrayList<>();
    //
    private float stdRaduis;

    // Output the List2float[] data
    public Float[] getListData(String type) {
        if (type.equals("original")) {
            Float[] data = new Float[originalRangeData.size()];
            originalRangeData.toArray(data);
            return data;
        }
        if (type.equals("bias")) {
            Float[] data = new Float[originalBiasData.size()];
            originalBiasData.toArray(data);
            return data;
        }
        return null;
    }

    // Clear the Data ArrayList
    public void clearOriginalData(){
        if (originalRangeData.size() != 0){
            originalRangeData = new ArrayList<>();
        }
        if (originalXData.size() != 0){
            originalRangeData = new ArrayList<>();
        }
        if (originalYData.size() != 0){
            originalRangeData = new ArrayList<>();
        }
        if (originalBiasData.size() != 0){
            originalBiasData = new ArrayList<>();
        }
    }

    // Add a number to the list
    public void addData(float range, float x, float y){
        originalRangeData.add(range);
        originalXData.add(x);
        originalYData.add(y);
        originalBiasData.add(range - stdRaduis);
    }

    // Add a Standard Radius
    public void addStdRadius(float radius){
        stdRaduis = radius;
    }

    // Return the total count of List
    public int getTotalCount(){
        return originalRangeData.size();
    }

    // Calculate the Summary
    public float getSum(){
        float outSum = 0;
        for (int i = 0; i < originalRangeData.size(); i++){
            outSum += originalRangeData.get(i);
        }
        return outSum;
    }

    // Calculate the average
    public float getAverage(){
        return getSum()/ originalRangeData.size();
    }

    // Calculate the standardDeviation
    public float getStandardDiviation(){
        float dVar = 0;
        for (int i = 0; i < originalRangeData.size(); i++){
            dVar += (originalRangeData.get(i) - getAverage()) * (originalRangeData.get(i) - getAverage());
        }
        return (float)Math.sqrt(dVar/originalRangeData.size());
    }

    // Calculate the Variance
    public float getVariance(){
        float dVar = 0;
        float dAve = getAverage();
        for (int i = 0; i < originalRangeData.size(); i++){
            dVar += (originalRangeData.get(i) - dAve)*(originalRangeData.get(i) - dAve);
        }
        return dVar/ originalRangeData.size();
    }

    // Calculate the (A, B) Radius through Least Squares Method
    public float[] getLeastSquaresResult(){
        int N = originalXData.size();
        float X1 = 0, Y1 = 0, X2 = 0, Y2 = 0, X3 = 0, Y3 = 0;
        float X1Y1 = 0, X1Y2 = 0, X2Y1 = 0;
        Float[] X = new Float[originalXData.size()];
        originalXData.toArray(X);
        Float[] Y = new Float[originalYData.size()];
        originalXData.toArray(Y);
        for (int i = 0; i < N; i++){
            X1 = X1 + X[i];
            Y1 = Y1 + Y[i];
            X2 = X2 + X[i]*X[i];
            Y2 = Y2 + Y[i]*Y[i];
            X3 = X3 + X[i]*X[i]*X[i];
            Y3 = Y3 + Y[i]*Y[i]*Y[i];
            X1Y1 = X1Y1 + X[i]*Y[i];
            X1Y2 = X1Y2 + X[i]*Y[i]*Y[i];
            X2Y1 = X2Y1 + X[i]*X[i]*Y[i];
        }
        float C = N * X2 - X1 * X1;
        float D = N * X1Y1 - X1Y1;
        float E = N * X3 + N * X1Y2 - (X2+Y2)*X1;
        float G = N*Y2 - Y1 * Y1;
        float H = N * X2Y1 + N * Y3 - (X2+Y2)*Y1;
        float a = (H*D-E*G)/(C*G-D*D);
        float b = (H*C-E*D)/(D*D-G*C);
        float c = -(a*X1+b*Y1+X2+Y2)/N;
        float A = a/(-2);
        float B = b/(-2);
        return new float[]{A, B, (float) Math.sqrt(a*a+b*b-4*c)/2};
    }
}
