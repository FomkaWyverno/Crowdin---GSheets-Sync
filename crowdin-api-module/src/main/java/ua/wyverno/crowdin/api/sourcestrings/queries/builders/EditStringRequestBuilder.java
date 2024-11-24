package ua.wyverno.crowdin.api.sourcestrings.queries.builders;

import com.crowdin.client.core.model.PatchOperation;
import com.crowdin.client.core.model.PatchRequest;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.enums.PathEditString;
import ua.wyverno.crowdin.api.util.edit.PatchEditOperation;

public class EditStringRequestBuilder {

    private PatchOperation op;
    private String path;
    private Object value;

    /**
     * Patch operation to perform
     * @param op replace/test
     * @return {@link EditStringRequestBuilder}
     */
    public EditStringRequestBuilder op(PatchEditOperation op) {
        this.op = op.getOp();
        return this;
    }

    /**
     * Enum: "/identifier" "/text" "/context" "/isHidden" "/maxLength" "/labelIds"
     * @param path
     * @return {@link EditStringRequestBuilder}
     */
    public EditStringRequestBuilder path(PathEditString path) {
        this.path = path.toString();
        return this;
    }

    /**
     * @param value string or object or integer or boolean
     * @return {@link EditStringRequestBuilder}
     */
    public EditStringRequestBuilder value(Object value) {
        this.value = value;
        return this;
    }

    public PatchRequest build() {
        PatchRequest patchRequest = new PatchRequest();
        patchRequest.setOp(this.op);
        patchRequest.setPath(this.path);
        patchRequest.setValue(this.value);
        return patchRequest;
    }
}
