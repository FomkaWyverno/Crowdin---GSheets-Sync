package ua.wyverno.localization.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleRow;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.google.sheets.util.GoogleSheetHeader;
import ua.wyverno.google.sheets.util.SheetA1NotationUtil;
import ua.wyverno.localization.model.SourceRegistryKey;
import ua.wyverno.localization.model.TranslationIdentifier;

import java.util.*;

@Component
public class SourceRegistryKeyParser {
    private final static Logger logger = LoggerFactory.getLogger(SourceRegistryKey.class);
    private final static String line = "---------------------------------------------";
    @Value("${key.parser.actor}")
    private String contextActorField;
    @Value("${key.parser.context}")
    private String contextField;
    @Value("${key.parser.timing}")
    private String contextTimingField;
    @Value("${key.parser.voice}")
    private String contextVoiceField;
    @Value("${key.parser.dub}")
    private String contextDubField;
    @Value("${key.parser.formatted.tool}")
    private String contextFormattedTool;
    @Value("${key.parser.formatted.tool.url}")
    private String contextFormattedToolURL;


    public Map<String, List<SourceRegistryKey>> parse(GoogleSpreadsheet spreadsheet) {
        Map<String, List<SourceRegistryKey>> keysBySheetName = new TreeMap<>();
        logger.debug("Start parsing spreadsheet to List<SourceRegistryKey>");

        List<GoogleSheet> sheets = spreadsheet.getSheets();

        for (GoogleSheet sheet : sheets) {
            String sheetName = sheet.getSheetName();
            logger.debug(line);
            logger.debug("Sheet-name: {}", sheetName);
            logger.debug(line);

            List<SourceRegistryKey> keys = new ArrayList<>();
            List<GoogleRow> rows = sheet.getRows();
            GoogleSheetHeader sheetHeader = new GoogleSheetHeader(sheet);
            int startKeyIndex = -1;
            Integer currentContainerId = null;
            String currentKey = "";
            String currentContext = "";
            StringBuilder currentSourceText = new StringBuilder();
            StringBuilder currentTranslateText = new StringBuilder();
            boolean isCurrentFormatted = false;
            boolean isApprove = false;
            // TODO: 30.11.2024 Додати переклад з аркуша до ключа перекладу
            for (int i = 1; i < rows.size(); i++) {
                GoogleRow row = rows.get(i);
                if (row.isEmpty()) continue; // Якщо порожній рядок скіпаємо
                // Беремо значення з таблиці
                RowDataExtractor rowExtractor = new RowDataExtractor(sheetHeader, row);

                this.logRowDetails(rowExtractor.getContainerId(), rowExtractor.getKey(), rowExtractor.getActor(), rowExtractor.getOriginalText(),
                        rowExtractor.getContext(), rowExtractor.getTiming(), rowExtractor.getVoice(), rowExtractor.getDub(), rowExtractor.getFormattedText());

                if (!rowExtractor.getContainerId().isEmpty() && !rowExtractor.getKey().isEmpty()) { // Якщо ключ та контейнер не порожній, означає ми попали на початок нового ключа
                    if (!currentKey.isEmpty() && currentContainerId != null) { // Якщо вже записується поточний ключ, то зберігаємо його
                        keys.add(this.buildKey(currentContainerId, currentKey, currentSourceText.toString(), currentContext,
                                sheetName, startKeyIndex, row.getIndex()-1, sheet.getColumnCount()-1));
                    }
                    // Оновлюємо інформацію про поточного ключа
                    startKeyIndex = row.getIndex();
                    currentContainerId = Integer.parseInt(rowExtractor.getContainerId());
                    currentKey = rowExtractor.getKey();


                    currentSourceText = new StringBuilder();
                    isCurrentFormatted = rowExtractor.getFormattedText() != null;
                    currentContext = this.buildContext(rowExtractor.getActor(), rowExtractor.getContext(), rowExtractor.getTiming(), rowExtractor.getVoice(),
                            rowExtractor.getDub(), isCurrentFormatted);

                    if (isCurrentFormatted) { // Якщо існує колонка з Formatted-Text тоді як вихідний рядок буде, ігровий текст з гри, з тегами
                        currentSourceText.append(rowExtractor.getGameText());
                    }
                }
                if (!isCurrentFormatted) currentSourceText.append(rowExtractor.getOriginalText()).append("\n"); // Якщо в аркуші немає колонки Formatted-Text, то додаємо беремо текст з Original-Text без тегів
            }

            if (!currentKey.isEmpty() && currentContainerId != null) { // Якщо вже записується поточний ключ, то зберігаємо його
                keys.add(this.buildKey(currentContainerId, currentKey, currentSourceText.toString(), currentContext,
                        sheetName, startKeyIndex, sheet.getLastRowIndexWithContent(), sheet.getColumnCount()-1));
            }
            // Кладемо лист з ключами перекладу де ключ це назва аркуша, а значення це лист з ключами перекладу
            keysBySheetName.put(sheetName, keys);
        }

        return keysBySheetName;
    }

    private String buildContext(String actor, String context, String timing, String voice, String dub, boolean isFormattedText) {
        StringBuilder contextBuilder = new StringBuilder();
        if (actor != null) {
            contextBuilder.append(this.contextActorField).append(": ").append(actor).append("\n");
        }
        if (!context.isEmpty()) {
            contextBuilder.append(this.contextField).append(": ")
                    .append(context).append("\n");
        }
        if (!context.isEmpty() && !timing.isEmpty()) {
            contextBuilder.append(this.contextTimingField).append(": ")
                    .append(timing).append("\n");
        }
        if (voice != null && !voice.isEmpty()) {
            contextBuilder.append(this.contextVoiceField).append(": ").append(voice).append("\n");
        }
        if (dub != null && !dub.isEmpty()) {
            contextBuilder.append(this.contextDubField).append(": ").append(dub).append("\n");
        }
        if (isFormattedText) {
            contextBuilder.append(this.contextFormattedTool).append(":\n")
                    .append(this.contextFormattedToolURL).append("\n");
        }

        return contextBuilder.deleteCharAt(contextBuilder.length()-1).toString();
    }

    private SourceRegistryKey buildKey(Integer containerId, String key, String sourceText, String context,
                                       String sheetName, int startKeyRowIndex, int endKeyRowIndex, int endKeyColumnIndex) {
        TranslationIdentifier identifier = new TranslationIdentifier(containerId, key);
        String locationA1 = SheetA1NotationUtil.rangeToA1Notation(sheetName, startKeyRowIndex, 0, endKeyRowIndex, endKeyColumnIndex);
        return new SourceRegistryKey(identifier, sourceText.replaceAll("\\n$", ""), context, locationA1);
    }

    private void logRowDetails(String containerId, String key, String actor,
                               String originalText, String context, String timing,
                               String voice, String dub, String formattedText) {
        if (logger.isTraceEnabled()) {
            logger.trace("Container-ID: {}\tKey: {}\tActor: {}\tText: {}\tContext: {}\tTiming: {}\tVoice: {}\tDub: {}\tFormatted-Text: {}",
                    truncateString(containerId, 5), truncateString(key, 10), truncateString(actor, 10),
                    truncateString(originalText, 15), truncateString(context, 10), truncateString(timing, 5),
                    truncateString(voice, 5), truncateString(dub, 5), truncateString(formattedText, 15));
        }
    }

    private String truncateString(String string, int maxLength) {
        return string != null && string.length() > maxLength
                ? string.substring(0, maxLength)+"..."
                : string;
    }
}
