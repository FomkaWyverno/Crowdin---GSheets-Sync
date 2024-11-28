package ua.wyverno.google.sheets.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleCell;
import ua.wyverno.google.sheets.model.GoogleRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class GoogleRowParser {

    @Autowired
    private GoogleCellParser cellParser;
    public GoogleRow parse(List<Object> row, int indexRow) {
        List<GoogleCell> cells = new ArrayList<>(row.size());
        for (int i = 0; i < row.size(); i++) {
            GoogleCell cell = this.cellParser.parse(row.get(i), i);
            cells.add(cell);
        }
        return new GoogleRow(cells, indexRow);
    }
}
