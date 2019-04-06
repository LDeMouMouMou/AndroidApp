package com.example.measureit.Part_NEW.DataSession;

public class LineChartProperties {

    private int linePointRadius = 2;
    private String lineOriginalPointShape = "circle";
    private String lineFilteredPointShape = "circle";
    private String[] lineChartDotShape = new String[]{"circle", "circle"};
    private int[] lineChartDotSize = new int[]{2, 2};
    private int[] lineChartLineSize = new int[]{2, 2};
    private boolean[] lineChartDotHasLines = new boolean[]{false, false};
    private boolean[] lineChartDotHasFilled = new boolean[]{false, false};
    private boolean[] lineChartLineHasFilled = new boolean[]{false, false};
    private boolean linePointHasLine = false;
    private boolean lineFilled = false;
    private boolean lineXHasLines = true;
    private boolean lineYHasLines = true;
    private boolean lineXPositionBottom = true;
    private boolean lineYPositionLeft = true;
    private boolean lineHasOriginalLine = true;
    private boolean lineHasFilteredLine = false;
    private boolean lineHasFittingLine = false;
    private boolean lineHasStandardLine = false;
    private String lineOriginalPointColorARGB = "FFFFCD41";
    private String lineFilteredPointColorARGB = "FF000000";
    private String lineStandardPointColorARGB = "FF0078FF";
    private String lineFittingPointColorARGB = "FF0028FF";


    public void setLinePointRadius(int pointRadius) {
        linePointRadius = pointRadius;
    }

    public int getLinePointRadius() {
        return  linePointRadius;
    }

    public void setLineFilled(boolean isFilled) {
        lineFilled = isFilled;
    }

    public boolean getLineFilled() {
        return lineFilled;
    }

    public void setLineXHasLines(boolean hasLines) {
        lineXHasLines = hasLines;
    }

    public boolean getLineXHasLines() {
        return lineXHasLines;
    }

    public void setLineYHasLines(boolean hasLines) {
        lineYHasLines = hasLines;
    }

    public boolean getLineYHasLines() {
        return lineYHasLines;
    }

    public void setLineXPositionBottom(boolean isBottom) {
        lineXPositionBottom = isBottom;
    }

    public boolean getLineXPositionBottom() {
        return lineXPositionBottom;
    }

    public void setLineYPositionLeft(boolean isLeft) {
        lineYPositionLeft = isLeft;
    }

    public boolean getLineYPositionLeft() {
        return lineYPositionLeft;
    }

    public void setLineOriginalPointShape(String shape) {
        lineOriginalPointShape = shape;
    }

    public String getLineOriginalPointShape() {
        return lineOriginalPointShape;
    }

    public void setLineChartDotShape(String shape, int position) {
        lineChartDotShape[position] = shape;
    }

    public String getLineChartPointShape(int position) {
        return lineChartDotShape[position];
    }

    public String[] getLineDotsPointShape() {
        return new String[]{lineOriginalPointShape, lineFilteredPointShape};
    }

    public void setLineChartDotSize(int size, int position) {
        lineChartDotSize[position] = size;
    }

    public int getLineChartDotSize(int position) {
        return lineChartDotSize[position];
    }

    public void setLineChartDotHasLines(boolean hasLines, int position) {
        lineChartDotHasLines[position] = hasLines;
    }

    public boolean getLineChartDotHasLines(int position) {
        return lineChartDotHasLines[position];
    }

    public void setLineChartDotHasFilled(boolean hasFilled, int position) {
        lineChartDotHasFilled[position] = hasFilled;
    }

    public boolean getLineChartDotHasFilled(int position) {
        return lineChartDotHasFilled[position];
    }

    public void setLineChartLineSize(int size, int position) {
        lineChartLineSize[position] = size;
    }

    public int getLineChartLineSize(int position) {
        return lineChartLineSize[position];
    }

    public void setLineChartLineHasFilled(boolean hasFilled, int position) {
        lineChartLineHasFilled[position] = hasFilled;
    }

    public boolean getLineChartLineHasFilled(int position) {
        return lineChartLineHasFilled[position];
    }

    public void setLinePointHasLine(boolean hasLine) {
        linePointHasLine = hasLine;
    }

    public boolean getLinePointHasLine() {
        return linePointHasLine;
    }

    public void setLineOriginalPointColorARGB(String colorARGB) {
        lineOriginalPointColorARGB = colorARGB;
    }

    public String getLineOriginalPointColorARGB() {
        return lineOriginalPointColorARGB;
    }

    public void setLineFilteredPointColorARGB(String colorARGB) {
        lineFilteredPointColorARGB = colorARGB;
    }

    public String getLineFilteredPointColorARGB() {
        return lineFilteredPointColorARGB;
    }

    public void setLineStandardPointColorARGB(String colorARGB) {
        lineStandardPointColorARGB = colorARGB;
    }

    public String getLineStandardPointColorARGB() {
        return lineStandardPointColorARGB;
    }

    public void setLineFittingPointColorARGB(String colorARGB) {
        lineFittingPointColorARGB = colorARGB;
    }

    public String getLineFittingPointColorARGB() {
        return lineFittingPointColorARGB;
    }

    public String[] getLineAllPointColorARGB() {
        return new String[]{lineOriginalPointColorARGB,
                lineFilteredPointColorARGB, lineStandardPointColorARGB, lineFittingPointColorARGB};
    }

    public void setLineHasOriginalLine(boolean hasLine) {
        lineHasOriginalLine = hasLine;
    }

    public boolean getLineHasOriginalLine() {
        return lineHasOriginalLine;
    }

    public void setLineHasFilteredLine(boolean hasLine) {
        lineHasFilteredLine = hasLine;
    }

    public boolean getLineHasFilteredLine() {
        return lineHasFilteredLine;
    }

    public void setLineHasFittingLine(boolean hasLine) {
        lineHasFittingLine = hasLine;
    }

    public boolean getLineHasFittingLine() {
        return lineHasFittingLine;
    }

    public void setLineHasStandardLine(boolean hasLine) {
        lineHasStandardLine = hasLine;
    }

    public boolean getLineHasStandardLine() {
        return lineHasStandardLine;
    }

}
