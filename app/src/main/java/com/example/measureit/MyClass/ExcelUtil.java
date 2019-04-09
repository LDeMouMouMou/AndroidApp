package com.example.measureit.MyClass;

import android.content.Context;

import com.example.measureit.Part_NEW.DataSession.ExcelDataItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtil {

    private static WritableCellFormat arial14format = null;
    private static WritableFont arial14font = null;
    private static WritableCellFormat arial12format = null;
    private static WritableFont arial12font = null;
    private final static String UTF8_ENCODING = "UTF-8";

    private void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(Alignment.CENTRE);
            arial14format.setBorder(Border.ALL, BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);
            arial12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            arial12format = new WritableCellFormat(arial12font);
            arial12format.setAlignment(Alignment.CENTRE);
            arial12format.setBorder(Border.ALL, BorderLineStyle.THIN);
            arial12format.setBackground(Colour.GRAY_25);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initExcel(String fileName, String sheetName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            sheet.addCell(new Label(0, 0, fileName, arial14format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial12format));
            }
            sheet.setRowView(0, 340);
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public <T> void writeObjListToExcel(List<T> objList, String fileName, Context context) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writableWorkbook = null;
            InputStream inputStream = null;
            try {
                WorkbookSettings workbookSettings = new WorkbookSettings();
                workbookSettings.setEncoding(UTF8_ENCODING);
                inputStream = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(inputStream);
                writableWorkbook = Workbook.createWorkbook(new File(fileName), workbook);
                WritableSheet sheet = writableWorkbook.getSheet(0);
                for (int i = 0; i < objList.size(); i++) {
                    ExcelDataItem excelDataItem = (ExcelDataItem) objList.get(i);
                    List<String> list = new ArrayList<>();
                    list.add(String.valueOf(excelDataItem.getAngle()));
                    list.add(String.valueOf(excelDataItem.getOriginalRange()));
                    for (int j = 0; j < list.size(); j++) {
                        sheet.addCell(new Label(i, j+1, list.get(j), arial12format));
                        if (list.get(j).length() <= 4) {
                            sheet.setColumnView(j, list.get(j).length()+8);
                        }
                        else {
                            sheet.setColumnView(j, list.get(j).length()+5);
                        }
                    }
                    sheet.setRowView(i+1, 350);
                }
                writableWorkbook.write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writableWorkbook != null) {
                    try {
                        writableWorkbook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
