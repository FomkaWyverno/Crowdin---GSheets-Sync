package ua.wyverno.localization.header;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.localization.config.LocalizationNameColumns;

@Component
public class TranslationSheetHeaderFactory {
    private final LocalizationNameColumns localizationNameColumns;

    @Autowired
    public TranslationSheetHeaderFactory(LocalizationNameColumns localizationNameColumns) {
        this.localizationNameColumns = localizationNameColumns;
    }

    public TranslationSheetHeader create(GoogleSheet sheet) {
        return new TranslationSheetHeader(sheet, this.localizationNameColumns);
    }
}
