package com.example.measureit.Part_NEW.DataSession;

public class DataConfigItem {

    private Boolean isTitle;
    private String titleName;
    private String itemName;

    public DataConfigItem(Boolean istitle, String itemname) {
        this.isTitle = istitle;
        this.itemName = itemname;
    }

    public void setIsTitle(Boolean title) {
        isTitle = title;
    }

    public Boolean getIsTitle() {
        return isTitle;
    }


    public void setItemName(String name) {
        itemName = name;
    }

    public String getItemName() {
        return itemName;
    }



}
