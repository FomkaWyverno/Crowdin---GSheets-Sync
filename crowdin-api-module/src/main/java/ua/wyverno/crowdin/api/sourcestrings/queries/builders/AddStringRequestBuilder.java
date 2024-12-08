package ua.wyverno.crowdin.api.sourcestrings.queries.builders;

import com.crowdin.client.sourcestrings.model.AddSourceStringRequest;

import java.util.List;

public class AddStringRequestBuilder {
    private String text;
    private long fileID;
    private String identifier;
    private String context;
    private Boolean isHidden;
    private Integer maxLength;
    private List<Long> labelIds;

    /**
     * @param text текст вихідного рядка
     * @return {@link AddStringRequestBuilder}
     */
    public AddStringRequestBuilder text(String text) {
        this.text = text;
        return this;
    }

    /**
     * @param fileID айді файла де має бути цей вихідний рядок
     * @return {@link AddStringRequestBuilder}
     */
    public AddStringRequestBuilder fileID(long fileID) {
        this.fileID = fileID;
        return this;
    }

    /**
     * @param identifier айді рядка
     * @return {@link AddStringRequestBuilder}
     */
    public AddStringRequestBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    /**
     * @param context контекст рядка
     * @return {@link AddStringRequestBuilder}
     */
    public AddStringRequestBuilder context(String context) {
        this.context = context;
        return this;
    }

    /**
     * @param hidden чи має бути він прихованим?
     * @return {@link AddStringRequestBuilder}
     */
    public AddStringRequestBuilder hidden(Boolean hidden) {
        isHidden = hidden;
        return this;
    }

    /**
     * @param maxLength максимальна довжина рядка
     * @return {@link AddStringRequestBuilder}
     */
    public AddStringRequestBuilder maxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    /**
     * @param labelIds айді лейблов
     * @return {@link AddStringRequestBuilder}
     */
    public AddStringRequestBuilder labelIds(List<Long> labelIds) {
        this.labelIds = labelIds;
        return this;
    }

    public AddSourceStringRequest build() {
        AddSourceStringRequest request = new AddSourceStringRequest();
        request.setText(this.text);
        request.setFileId(this.fileID);
        request.setIdentifier(this.identifier);
        request.setContext(this.context);
        request.setIsHidden(this.isHidden);
        request.setMaxLength(this.maxLength);
        request.setLabelIds(this.labelIds);

        return request;
    }

    @Override
    public String toString() {
        return "{\"AddStringRequestBuilder\":{"
                + "\"text\":\"" + text + "\",\n"
                + "\"fileID\":\"" + fileID + "\",\n"
                + "\"identifier\":\"" + identifier + "\",\n"
                + "\"context\":\"" + context + "\",\n"
                + "\"isHidden\":\"" + isHidden + "\",\n"
                + "\"maxLength\":\"" + maxLength + "\",\n"
                + "\"labelIds\":" + labelIds
                + "}}";
    }
}
