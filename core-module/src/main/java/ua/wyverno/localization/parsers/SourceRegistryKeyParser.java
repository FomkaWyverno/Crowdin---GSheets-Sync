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


    public Map<String, SourceRegistryKey> parse(GoogleSpreadsheet spreadsheet) {
        Map<String, SourceRegistryKey> keysBySheetName = new TreeMap<>();
        logger.debug("Start parsing spreadsheet to List<SourceRegistryKey>");

        List<GoogleSheet> sheets = spreadsheet.getSheets();

        for (GoogleSheet sheet : sheets) {
            String sheetName = sheet.getSheetName();
            logger.debug(line);
            logger.debug("Sheet-name: {}", sheetName);
            logger.debug(line);

            List<GoogleRow> rows = sheet.getRows();
            GoogleSheetHeader sheetHeader = new GoogleSheetHeader(sheet);
            int startKeyIndex = -1;
            String currentContainerId = "";
            String currentKey = "";
            String currentContext = "";
            StringBuilder currentSourceText = new StringBuilder();

            for (int i = 1; i < rows.size(); i++) { // TODO: 28.11.2024 Виправити баг, який IndexOutOfBoundsException при парсингу таблиці
                GoogleRow row = rows.get(i);
                if (row.isEmpty()) continue; // Якщо порожній рядок скіпаємо
                String containerId = sheetHeader.getValue(row, "Container-ID");
                String key = sheetHeader.getValue(row, "Key-Translate");
                String actor = sheetHeader.getValueIfExists(row, "Актор");
                String sourceText = sheetHeader.getValue(row, "Original-Text");
                String context = sheetHeader.getValue(row, "Context");
                String timing = sheetHeader.getValue(row, "Timing");
                String voice = sheetHeader.getValueIfExists(row, "Voice");
                String dub = sheetHeader.getValueIfExists(row, "Dub");
                String formattedText = sheetHeader.getValueIfExists(row, "Formatted-Text");

                this.logRowDetails(containerId, key, actor, sourceText, context, timing, voice, dub, formattedText);

                if (!containerId.isEmpty() && !key.isEmpty()) { // Якщо ключ та контейнер не порожній, означає ми попали на початок нового ключа
                    if (!currentKey.isEmpty() && !currentContainerId.isEmpty()) { // Якщо вже записується поточний ключ, то зберігаємо його
                        TranslationIdentifier identifier = new TranslationIdentifier(Integer.parseInt(containerId), currentKey);
                        String locationA1 = SheetA1NotationUtil.rangeToA1Notation(sheetName, startKeyIndex, 0, row.getIndex(), row.getCells().size());
                        SourceRegistryKey sourceRegistryKey =
                                new SourceRegistryKey(identifier,
                                        currentSourceText.deleteCharAt(currentSourceText.length()-1).toString(),
                                        currentContext, locationA1);
                        keysBySheetName.put(sheetName, sourceRegistryKey);
                    }
                    // Оновлюємо інформацію про поточного ключа
                    startKeyIndex = row.getIndex();
                    currentContainerId = containerId;
                    currentKey = key;

                    currentContext = this.buildContext(actor, context, timing, voice, dub, formattedText != null);
                    currentSourceText = new StringBuilder();
                }

                currentSourceText.append(sourceText).append("\n");
            }
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
