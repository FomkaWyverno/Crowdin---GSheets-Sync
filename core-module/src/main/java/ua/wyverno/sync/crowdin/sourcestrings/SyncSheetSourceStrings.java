package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.sourcestrings.model.SourceString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.localization.model.TranslateRegistryKey;
import ua.wyverno.localization.parsers.GSheetTranslateRegistryKeyParser;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SyncSheetSourceStrings {
    private final static Logger logger = LoggerFactory.getLogger(SyncSheetSourceStrings.class);

    private final GSheetTranslateRegistryKeyParser parser;
    private final CrowdinSourceStringsManager sourceStringsManager;

    @Autowired
    public SyncSheetSourceStrings(GSheetTranslateRegistryKeyParser parser, CrowdinSourceStringsManager sourceStringsManager) {
        this.parser = parser;
        this.sourceStringsManager = sourceStringsManager;
    }

    /**
     * Синхронізує вміст аркуша, з вмістом файлу у Кроудіні
     * @param file файл
     * @param sheet аркуш
     * @param fileStrings всі рядкі які існують у Проєкті
     */
    public SyncSheetSourceStringsResult synchronizeToSheet(FileInfo file, GoogleSheet sheet, List<SourceString> fileStrings) {
        logger.debug("Start parsing sheet.");
        List<GSheetTranslateRegistryKey> keys = this.parser.parseSheet(sheet);
        logger.debug("Finding exists source strings for keys."); // Збираємо рядки які існують
        Map<SourceString, GSheetTranslateRegistryKey> existsKeyBySourceString = this.collectExistsSourceStrings(fileStrings, keys);
        logger.debug("Starting exists source strings synchronization.");
        logger.debug("Starting prepare AddStringRequest for missing source string."); // Підготовлюємо запит на створення рядків які пропущені
        List<AddStringRequestBuilder> preparedAddStringRequests = this.prepareRequestMissingSourceString(keys, existsKeyBySourceString, file);

        logger.debug("Finish synchronize to Sheet. Collect exists key size: {}, PreparedAddStringRequest size: {}", existsKeyBySourceString.size(), preparedAddStringRequests.size());
        return new SyncSheetSourceStringsResult(existsKeyBySourceString.keySet().stream().toList(), preparedAddStringRequests); // Відаємо результат виконання синхронізації
    }

    /**
     * Збирає рядки які існують в аркуші
     * @param fileStrings рядки у файлі
     * @param keys ключі з аркуша
     * @return Повертає мапу де ключ це рядок у Кроудіні, значення це відповідний ключ в аркуші
     */
    private Map<SourceString, GSheetTranslateRegistryKey> collectExistsSourceStrings(List<SourceString> fileStrings, List<GSheetTranslateRegistryKey> keys) {
        Map<String, SourceString> stringById = fileStrings.stream()
                .collect(Collectors.toMap(SourceString::getIdentifier, Function.identity()));
        return keys.stream()
                .filter(key -> stringById.containsKey(key.identifier().toString()))
                .collect(Collectors.toMap(
                        key -> stringById.get(key.identifier().toString()),
                        Function.identity()));
    }

    /**
     * Підготовлює запит для створення вихідних рядків які відсутні
     * @param keys ключі у цьому аркуші
     * @param existsKeyBySourceString рядки які існують вже в аркуші
     * @return Повертає лист з підготовленими запитами для створення рядків які відсутні
     */
    private List<AddStringRequestBuilder> prepareRequestMissingSourceString(List<GSheetTranslateRegistryKey> keys, Map<SourceString, GSheetTranslateRegistryKey> existsKeyBySourceString, FileInfo file) {
        List<String> existsStringId = existsKeyBySourceString.keySet().stream()
                .map(SourceString::getIdentifier)
                .toList();

        List<GSheetTranslateRegistryKey> missingKeys = keys.stream()
                .filter(key -> !existsStringId.contains(key.identifier().toString()))
                .toList();

        return missingKeys.stream()
                .map(key -> new AddStringRequestBuilder()
                        .fileID(file.getId())
                        .text(key.originalText())
                        .context(key.context())
                        .identifier(key.identifier().toString()))
                .toList();
    }
}
