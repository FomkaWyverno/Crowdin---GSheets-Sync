package ua.wyverno.google.sheets.model;

import java.util.List;

public class GoogleRow {
    private final int index;
    private final List<GoogleCell> cells;
    private final int columnCount;

    public GoogleRow(List<GoogleCell> cells, int index, int columnCount) {
        this.index = index;
        this.cells = cells;
        this.columnCount = columnCount;
    }

    public int getIndex() {
        return index;
    }

    public List<GoogleCell> getCells() {
        return cells;
    }

    public GoogleCell getCell(int index) {
        if (this.cells.size() > index) return this.cells.get(index);
        if (this.columnCount > index) return new GoogleCell("", index);
        String errorMsg = String.format("Cell index %d out of bounds for length %d in sheet", index, this.columnCount);
        throw new IndexOutOfBoundsException(errorMsg);
    }

    /**
     * Повертає максимальну кількість колоном у таблиці.<br/>
     * Це кількість реальної кількості колонок у таблиці, вони можуть бути порожніми
     * @return кількість колонок у таблиці
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * @return Індекс останньої колонки де є реальні дані
     */
    public int getLastColumnIndexWithContent() {
        return this.cells.size()-1;
    }

    public boolean isEmpty() {
        return this.getCells().stream().allMatch(GoogleCell::isEmpty);
    }
}
