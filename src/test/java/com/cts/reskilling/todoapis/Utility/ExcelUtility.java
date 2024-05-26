package com.cts.reskilling.todoapis.Utility;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelUtility {
    private static final String FILE_NAME = "InputData.xlsx";

    public static Object[][] readExcel(String sheetName) throws IOException {
        FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
        Workbook workbook = new HSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheet(sheetName);
        Iterator<Row> iterator = datatypeSheet.iterator();

        int rowCount = datatypeSheet.getPhysicalNumberOfRows();
        int columnCount = datatypeSheet.getRow(0).getPhysicalNumberOfCells();

        Object[][] data = new Object[rowCount - 1][columnCount];

        int i = 0;
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            if (currentRow.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = currentRow.iterator();
            int j = 0;
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                if (currentCell.getCellType() == CellType.STRING) {
                    data[i][j] = currentCell.getStringCellValue();
                } else if (currentCell.getCellType() == CellType.NUMERIC) {
                    data[i][j] = currentCell.getNumericCellValue();
                }
                j++;
            }
            i++;
        }
        workbook.close();
        return data;
    }
}

