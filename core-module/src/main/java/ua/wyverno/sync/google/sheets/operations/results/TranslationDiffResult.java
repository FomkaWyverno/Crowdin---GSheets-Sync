package ua.wyverno.sync.google.sheets.operations.results;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

public record TranslationDiffResult(int countTranslationKeyChange, List<ValueRange> valueRanges,
                                    java.util.Map<ua.wyverno.localization.model.key.GSheetTranslateKey,ua.wyverno.crowdin.model.CrowdinTranslation> badCountValuesTranslations) {
}
