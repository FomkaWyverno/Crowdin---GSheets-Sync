package ua.wyverno.crowdin.api.sourcestrings.queries.batch;

import com.crowdin.client.sourcestrings.model.AddSourceStringRequest;

import java.util.List;

public class AddSourceStringRequestBuilder {
    private String text;
    private long fileID;
    private String identifier;
    private String context;
    private Boolean isHidden;
    private Integer maxLength;
    private List<Long> labelIds;

    public AddSourceStringRequestBuilder text(String text) {
        this.text = text;
        return this;
    }

    public AddSourceStringRequestBuilder fileID(long fileID) {
        this.fileID = fileID;
        return this;
    }

    public AddSourceStringRequestBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public AddSourceStringRequestBuilder context(String context) {
        this.context = context;
        return this;
    }

    public AddSourceStringRequestBuilder hidden(Boolean hidden) {
        isHidden = hidden;
        return this;
    }

    public AddSourceStringRequestBuilder maxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public AddSourceStringRequestBuilder labelIds(List<Long> labelIds) {
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
}
