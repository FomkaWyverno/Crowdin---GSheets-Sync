package ua.wyverno.crowdin.model;

import com.crowdin.client.sourcestrings.model.SourceString;

public class CrowdinTranslation {
    private final SourceString sourceString;
    private final String translation;
    private final boolean isApprove;

    public CrowdinTranslation(SourceString sourceString, String translation, boolean isApprove) {
        this.sourceString = sourceString;
        this.translation = translation;
        this.isApprove = isApprove;
    }

    public String getTranslation() {
        return translation;
    }

    public boolean isApprove() {
        return isApprove;
    }
}
