package ua.wyverno.localization.header;

import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.util.GoogleSheetHeader;
import ua.wyverno.localization.config.LocalizationNameColumns;

public class TranslationSheetHeader extends GoogleSheetHeader {

    private final LocalizationNameColumns localizationNameColumns;

    protected TranslationSheetHeader(GoogleSheet sheet, LocalizationNameColumns localizationNameColumns) {
        super(sheet);
        this.localizationNameColumns = localizationNameColumns;
    }

    public boolean hasFormattedColumn() {
        return this.containsColumn(this.localizationNameColumns.getFormattedText());
    }

    public int getFormattedColumnIndex() {
        return this.getColumnIndex(this.localizationNameColumns.getFormattedText());
    }

    public int getTranslateColumnIndex() {
        return this.getColumnIndex(this.localizationNameColumns.getTranslateText());
    }

    public int getEditColumnIndex() {
        return this.getColumnIndex(this.localizationNameColumns.getEditText());
    }
}
