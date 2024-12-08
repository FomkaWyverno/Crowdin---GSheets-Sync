package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.sourcestrings.model.SourceString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.ReplaceBatchStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.enums.PathEditString;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.localization.parsers.GSheetTranslateRegistryKeyParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SyncSheetSourceStrings {
    private final static Logger logger = LoggerFactory.getLogger(SyncSheetSourceStrings.class);

    private final GSheetTranslateRegistryKeyParser parser;

    @Autowired
    public SyncSheetSourceStrings(GSheetTranslateRegistryKeyParser parser) {
        this.parser = parser;
    }

    /**
     * Синхронізує вміст аркуша, з вмістом файлу у Кроудіні
     * @param file файл
     * @param sheet аркуш
     * @param fileStrings всі рядкі які існують у Проєкті
     */
    public SyncSheetSourceStringsResult synchronizeToSheet(FileInfo file, GoogleSheet sheet, List<SourceString> fileStrings) {
        this.updateThreadName(sheet);
        logger.debug("Start parsing sheet.");
        List<GSheetTranslateRegistryKey> keys = this.parser.parseSheet(sheet);
        logger.debug("Finding exists source strings for keys."); // Збираємо рядки які існують
        Map<SourceString, GSheetTranslateRegistryKey> existsKeyBySourceString = this.collectExistsSourceStrings(fileStrings, keys);
        logger.debug("Starting exists source strings synchronization.");
        List<ReplaceBatchStringRequestBuilder> preparedReplaceStringRequest = this.syncContentStringAndPrepareEditRequest(existsKeyBySourceString);
        logger.debug("Starting prepare AddStringRequest for missing source string."); // Підготовлюємо запит на створення рядків які пропущені
        List<AddStringRequestBuilder> preparedAddStringRequests = this.prepareRequestMissingSourceString(keys, existsKeyBySourceString, file);

        logger.info("""
                Finish synchronize to Sheet.
                Collect exists key size: {}, PreparedAddStringRequests size: {}, PreparedReplaceStringRequests size: {}""",
                existsKeyBySourceString.size(),
                preparedAddStringRequests.size(),
                preparedReplaceStringRequest.size());
        return new SyncSheetSourceStringsResult(existsKeyBySourceString.keySet().stream().toList(), preparedAddStringRequests, preparedReplaceStringRequest); // Відаємо результат виконання синхронізації
    }

    /**
     * Додає до назви потоку назву аркуша для логування
     * @param sheet аркуш
     */
    private void updateThreadName(GoogleSheet sheet) {
        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();
        // Беремо базову назву потоку
        String baseName = threadName.replaceFirst("/Sheet.*$", "");
        // Оновлюємо назву потоку додаємо назву аркуша у потік
        currentThread.setName(baseName + "/Sheet-"+sheet.getSheetName());
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

    /**
     * Синхронізує вміст рядка, такі як контекст, та оригінальний рядок.
     * @param existsKeyInCrowdin мапа рядків як існують у Кроудіні, де Ключ це рядок у Кроудіні, де значення відповідний ключ в Аркуші
     * @return Лист з підготовленими запитами для виконання змін рядка.
     */
    private List<ReplaceBatchStringRequestBuilder> syncContentStringAndPrepareEditRequest(Map<SourceString, GSheetTranslateRegistryKey> existsKeyInCrowdin) {
        List<ReplaceBatchStringRequestBuilder> requestsEdit = new ArrayList<>();

        existsKeyInCrowdin.forEach((string, key) -> {
            if (!string.getText().equals(key.originalText())) {
                logger.trace("String id: {}. Need replace text from: {} to: {}", string.getIdentifier(), string.getText(), key.originalText());
                requestsEdit.add(new ReplaceBatchStringRequestBuilder()
                        .stringID(string.getId())
                        .path(PathEditString.TEXT)
                        .value(key.originalText()));
            }
            Optional<String> stringContext = Optional.ofNullable(string.getContext());
            if (!stringContext.orElse("").equals(key.context().trim())) {
                logger.trace("String id: {}. Need replace context from: {} to: {}", string.getIdentifier(), stringContext, key.context());
                requestsEdit.add(new ReplaceBatchStringRequestBuilder()
                        .stringID(string.getId())
                        .path(PathEditString.CONTEXT)
                        .value(key.context()));
            }

        });

        return requestsEdit;
    }
}
