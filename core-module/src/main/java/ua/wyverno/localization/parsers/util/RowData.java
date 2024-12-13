package ua.wyverno.localization.parsers.util;

import org.springframework.lang.Nullable;
import ua.wyverno.google.sheets.model.GoogleRow;
import ua.wyverno.google.sheets.util.GoogleSheetHeader;

public class RowData {
    private String containerId;
    private String key;
    private String actor;
    private String gameText;
    private String originalText;
    private String translateText;
    private String editText;
    private String context;
    private String timing;
    private String voice;
    private String dub;
    private String formattedText;

    protected RowData() {}

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


    protected RowData setContainerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    protected RowData setKey(String key) {
        this.key = key;
        return this;
    }

    protected RowData setActor(String actor) {
        this.actor = actor;
        return this;
    }

    protected RowData setGameText(String gameText) {
        this.gameText = gameText;
        return this;
    }

    protected RowData setOriginalText(String originalText) {
        this.originalText = originalText;
        return this;
    }

    protected RowData setTranslateText(String translateText) {
        this.translateText = translateText;
        return this;
    }

    protected RowData setEditText(String editText) {
        this.editText = editText;
        return this;
    }

    protected RowData setContext(String context) {
        this.context = context;
        return this;
    }

    protected RowData setTiming(String timing) {
        this.timing = timing;
        return this;
    }

    protected RowData setVoice(String voice) {
        this.voice = voice;
        return this;
    }

    protected RowData setDub(String dub) {
        this.dub = dub;
        return this;
    }

    protected RowData setFormattedText(String formattedText) {
        this.formattedText = formattedText;
        return this;
    }
}


