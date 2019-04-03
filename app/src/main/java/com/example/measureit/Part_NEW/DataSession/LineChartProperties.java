package com.example.measureit.Part_NEW.DataSession;

public class LineChartProperties {

    private int linePointRadius = 2;
    private String linePointShape = "circle";
    private boolean linePointHasLine = false;
    private boolean lineFilled = false;
    private boolean lineXHasLines = true;
    private boolean lineYHasLines = true;
    private boolean lineXPositionBottom = true;
    private boolean lineYPositionLeft = true;

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

    public void setLinePointShape(String shape) {
        linePointShape = shape;
    }

    public String getLinePointShape() {
        return linePointShape;
    }

    public void setLinePointHasLine(boolean hasLine) {
        linePointHasLine = hasLine;
    }

    public boolean getLinePointHasLine() {
        return linePointHasLine;
    }

}
