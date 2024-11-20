package ua.wyverno.crowdin.api.sourcefiles.directories.edit;

import com.crowdin.client.core.model.PatchOperation;
import com.crowdin.client.core.model.PatchRequest;

public class PatchRequestBuilder {
    private PatchOperation op;
    private EditPath path;
    private Object value;

    public PatchRequestBuilder op(PatchOperation op) {
        this.op = op;
        return this;
    }

    public PatchRequestBuilder path(EditPath path) {
        this.path = path;
        return this;
    }

    public PatchRequestBuilder value(String value) {
        this.value = value;
        return this;
    }
    public PatchRequestBuilder value(Integer value) {
        this.value = value;
        return this;
    }
    public PatchRequest build() {
        PatchRequest patchRequest = new PatchRequest();
        patchRequest.setOp(this.op);
        patchRequest.setPath(this.path.getValue());
        patchRequest.setValue(this.value);
        return patchRequest;
    }
}
