package com.example.measureit.Part_NEW.DataSession;

public class FilterProperties {

    private String filterMode;
    private String filterDisplayMode;
    private int filterKernal;

    public FilterProperties(int filterKernal, String filterMode, String filterDisplayMode) {
        this.filterKernal = filterKernal;
        this.filterMode = filterMode;
        this.filterDisplayMode = filterDisplayMode;
    }

    public void setFilterMode(String mode) {
        filterMode = mode;
    }

    public String getFilterMode() {
        return filterMode;
    }

    public void setFilterDisplayMode(String mode) {
        filterDisplayMode = mode;
    }

    public String getFilterDisplayMode() {
        return filterDisplayMode;
    }

    public void setFilterKernal(int kernal) {
        filterKernal = kernal;
    }

    public int getFilterKernal() {
        return filterKernal;
    }

}
