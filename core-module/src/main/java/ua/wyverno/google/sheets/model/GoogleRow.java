package ua.wyverno.google.sheets.model;

import java.util.List;

public class GoogleRow {
    private int index;
    private List<GoogleCell> cells;

    public GoogleRow(List<GoogleCell> cells, int index) {
        this.index = index;
        this.cells = cells;
    }

    public int getIndex() {
        return index;
    }

    public List<GoogleCell> getCells() {
        return cells;
    }

    public GoogleCell getCell(int index) {
        return this.cells.get(index);
    }
}
