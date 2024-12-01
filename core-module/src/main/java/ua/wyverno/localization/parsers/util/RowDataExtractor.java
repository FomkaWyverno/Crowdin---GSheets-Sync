package ua.wyverno.localization.parsers.util;

import org.springframework.lang.Nullable;
import ua.wyverno.google.sheets.model.GoogleRow;
import ua.wyverno.google.sheets.util.GoogleSheetHeader;

public class RowDataExtractor {
    private final String containerId;
    private final String key;
    private final String actor;
    private final String gameText;
    private final String originalText;
    private final String translateText;
    private final String editText;
    private final String context;
    private final String timing;
    private final String voice;
    private final String dub;
    private final String formattedText;

    public RowDataExtractor(GoogleSheetHeader header, GoogleRow row) {
        this.containerId = header.getValue(row, "Container-ID");
        this.key = header.getValue(row, "Key-Translate");
        this.actor = header.getValueIfExists(row, "Актор");
        this.gameText = header.getValue(row, "Game-Text");
        this.originalText = header.getValue(row, "Original-Text");
        this.translateText = header.getValue(row, "Translate-Text");
        this.editText = header.getValue(row, "Edit-Text");
        this.context = header.getValue(row, "Context");
        this.timing = header.getValue(row, "Timing");
        this.voice = header.getValueIfExists(row, "Voice");
        this.dub = header.getValueIfExists(row, "Dub");
        this.formattedText = header.getValueIfExists(row, "Formatted-Text");
    }

    /**
     * @return Айді контейнера ключа перекладу
     */
    public String getContainerId() {
        return containerId;
    }

    /**
     * @return Ключ перекладу
     */
    public String getKey() {
        return key;
    }

    /**
     * @return роль Актора, поверне null якщо колонки не існує в аркуші.
     */
    @Nullable
    public String getActor() {
        return actor;
    }

    /**
     * @return ігровий текст, не оброблений текст з тегами.
     */
    public String getGameText() {
        return gameText;
    }

    /**
     * @return оригінальний текст, без тегів, кожен тег замінений на перенесення рядка.
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * @return переклад для поточного рядка з колонки Перекладачів
     */
    public String getTranslateText() {
        return translateText;
    }

    /**
     * @return переклад для поточного рядка з колонки редакторів
     */
    public String getEditText() {
        return editText;
    }

    /**
     * @return текст з колонки Context - контекст про переклад поточного ключа перекладу.
     */
    public String getContext() {
        return context;
    }

    /**
     * @return текст з колонки таймінга, використовується для зазначення таймінга на відео.
     */
    public String getTiming() {
        return timing;
    }

    /**
     * @return текст з колонки Voice (Оригінальна аудіодоріжка) якщо існує, якщо не існує колонки в аркуші буде null
     */
    @Nullable
    public String getVoice() {
        return voice;
    }

    /**
     * @return текст к колонки Dub (Дубляж) якщо існує, якщо не існує колонки в аркуші буде null
     */
    @Nullable
    public String getDub() {
        return dub;
    }

    /**
     * @return текст з колонки Formatted-Text, переклад з тегами
     */
    @Nullable
    public String getFormattedText() {
        return formattedText;
    }
}


