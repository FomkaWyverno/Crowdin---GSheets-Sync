package ua.wyverno.localization.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalizationNameColumns {

    @Value("${localization.column.containerId}")
    private String containerId;
    @Value("${localization.column.key}")
    private String key;
    @Value("${localization.column.actor}")
    private String actor;
    @Value("${localization.column.gameText}")
    private String gameText;
    @Value("${localization.column.originalText}")
    private String originalText;
    @Value("${localization.column.translateText}")
    private String translateText;
    @Value("${localization.column.editText}")
    private String editText;
    @Value("${localization.column.context}")
    private String context;
    @Value("${localization.column.timing}")
    private String timing;
    @Value("${localization.column.voice}")
    private String voice;
    @Value("${localization.column.dub}")
    private String dub;
    @Value("${localization.column.formattedText}")
    private String formattedText;

    public String getContainerId() {
        return containerId;
    }

    public String getKey() {
        return key;
    }

    public String getActor() {
        return actor;
    }

    public String getGameText() {
        return gameText;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getTranslateText() {
        return translateText;
    }

    public String getEditText() {
        return editText;
    }

    public String getContext() {
        return context;
    }

    public String getTiming() {
        return timing;
    }

    public String getVoice() {
        return voice;
    }

    public String getDub() {
        return dub;
    }

    public String getFormattedText() {
        return formattedText;
    }
}
