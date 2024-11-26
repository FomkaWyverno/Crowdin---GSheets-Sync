package ua.wyverno.google.sheets.parsers;

import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleCell;

@Component
public class GoogleCellParser {
    public GoogleCell parse(Object value, int index) {
        return new GoogleCell(value.toString(), index);
    }
}
