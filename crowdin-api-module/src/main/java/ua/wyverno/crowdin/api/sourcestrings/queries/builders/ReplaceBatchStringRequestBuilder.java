package ua.wyverno.crowdin.api.sourcestrings.queries.builders;

import com.crowdin.client.core.model.PatchOperation;
import com.crowdin.client.core.model.PatchRequest;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.enums.PathEditString;

public class ReplaceBatchStringRequestBuilder {
    private Long stringID;
    private String path;
    private Object value;

    /**
     * @param stringID рядок який потрібно змінити. Обов'язковий рядок.
     * @return {@link EditStringRequestBuilder}
     */
    public ReplaceBatchStringRequestBuilder stringID(Long stringID) {
        this.stringID = stringID;
        return this;
    }

    /**
     * Enum: "/identifier" "/text" "/context" "/isHidden" "/maxLength" "/labelIds"
     * @param path що саме потрібно змінити. Обов'язковий параметр.
     * @return {@link EditStringRequestBuilder}
     */
    public ReplaceBatchStringRequestBuilder path(PathEditString path) {
        this.path = path.toString();
        return this;
    }

    /**
     * @param value string or object or integer or boolean / Обов'язковий параметр.
     * @return {@link EditStringRequestBuilder}
     */
    public ReplaceBatchStringRequestBuilder value(Object value) {
        this.value = value;
        return this;
    }

    public PatchRequest build() {
        PatchRequest patchRequest = new PatchRequest();
        patchRequest.setOp(PatchOperation.REPLACE);
        patchRequest.setPath("/"+this.stringID + this.path);
        patchRequest.setValue(this.value);
        return patchRequest;
    }
}
