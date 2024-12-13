package ua.wyverno.localization.parsers.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleRow;
import ua.wyverno.google.sheets.util.GoogleSheetHeader;

@Component
public class RowDataExtractor {
    private final LocalizationNameColumns localizationNameColumns;

    @Autowired
    public RowDataExtractor(LocalizationNameColumns localizationNameColumns) {
        this.localizationNameColumns = localizationNameColumns;
    }

    public RowData extract(GoogleSheetHeader header, GoogleRow row) {
        RowData rowData = new RowData();

        rowData.setContainerId(header.getValue(row, this.localizationNameColumns.getContainerId()));
        rowData.setKey(header.getValue(row, this.localizationNameColumns.getKey()));
        rowData.setActor(header.getValueIfExists(row, this.localizationNameColumns.getActor()));
        rowData.setGameText(header.getValue(row, this.localizationNameColumns.getGameText()));
        rowData.setOriginalText(header.getValue(row, this.localizationNameColumns.getOriginalText()));
        rowData.setTranslateText(header.getValue(row, this.localizationNameColumns.getTranslateText()));
        rowData.setEditText(header.getValue(row, this.localizationNameColumns.getEditText()));
        rowData.setContext(header.getValue(row, this.localizationNameColumns.getContext()));
        rowData.setTiming(header.getValue(row, this.localizationNameColumns.getTiming()));
        rowData.setVoice(header.getValueIfExists(row, this.localizationNameColumns.getVoice()));
        rowData.setDub(header.getValueIfExists(row, this.localizationNameColumns.getDub()));
        rowData.setFormattedText(header.getValueIfExists(row, this.localizationNameColumns.getFormattedText()));

        return rowData;
    }
}
