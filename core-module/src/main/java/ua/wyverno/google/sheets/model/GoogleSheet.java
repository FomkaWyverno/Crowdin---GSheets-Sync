package ua.wyverno.google.sheets.model;

import java.util.Collections;
import java.util.List;

public class GoogleSheet {
    private final Integer sheetId;
    private final String sheetName;
    private final List<GoogleRow> rows;
    private final int rowCount;
    private final int columnCount;

    public GoogleSheet(Integer sheetId, String sheetName, List<GoogleRow> rows, int rowCount, int columnCount) {
        this.sheetId = sheetId;
        this.sheetName = sheetName;
        this.rows = Collections.unmodifiableList(rows);
        this.rowCount = rowCount;
        this.columnCount = columnCount;
    }

    public Integer getSheetId() {
        return sheetId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public List<GoogleRow> getRows() {
        return rows;
    }

    public GoogleRow getRow(int index) {
        if (this.rows.size() > index) return this.rows.get(index);
        if (this.rowCount > index) return new GoogleRow(Collections.emptyList(), index, this.columnCount);
        String errorMsg = String.format("Row index %d out of bounds for length %d in sheet", index, this.columnCount);
        throw new IndexOutOfBoundsException(errorMsg);
    }

    /**
     * Повертає максимальну кількість рядків у таблиці.<br/>
     * Ці рядки можуть бути порожніми.
     * @return кількість рядків в таблиці
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Повертає максимальну кількість колоном у таблиці.<br/>
     * Це кількість реальної кількості колонок у таблиці, вони можуть бути порожніми
     * @return кількість колонок у таблиці
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * @return Індекс останнього рядка де є реальні дані
     */
    public int getLastRowIndexWithContent() {
        return this.rows.size() - 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoogleSheet sheet = (GoogleSheet) o;

        if (rowCount != sheet.rowCount) return false;
        if (columnCount != sheet.columnCount) return false;
        if (!sheetId.equals(sheet.sheetId)) return false;
        if (!sheetName.equals(sheet.sheetName)) return false;
        return rows.equals(sheet.rows);
    }

    @Override
    public int hashCode() {
        int result = sheetId.hashCode();
        result = 31 * result + sheetName.hashCode();
        result = 31 * result + rows.hashCode();
        result = 31 * result + rowCount;
        result = 31 * result + columnCount;
        return result;
    }

    @Override
    public String toString() {
        return this.sheetName;
    }
}
