package ua.wyverno.crowdin.api.sourcefiles.files.queries.edit;

import com.crowdin.client.core.model.PatchRequest;
import ua.wyverno.crowdin.api.util.edit.PatchEditOperation;

import java.util.List;

public class PatchFileRequestBuilder {
    private PatchEditOperation op;
    private EditFilePath path;
    private Object value;

    public PatchFileRequestBuilder op(PatchEditOperation op) {
        this.op = op;
        return this;
    }

    public PatchFileRequestBuilder path(EditFilePath path) {
        this.path = path;
        return this;
    }

    public PatchFileRequestBuilder value(String value) {
        this.value = value;
        return this;
    }
    public PatchFileRequestBuilder value(Integer value) {
        this.value = value;
        return this;
    }
    public PatchFileRequestBuilder valuesIntegers(List<Integer> value) {
        this.value = value;
        return this;
    }

    public PatchFileRequestBuilder valueStrings(List<String> value) {
        this.value = value;
        return this;
    }

    public PatchRequest build() {
        PatchRequest patchRequest = new PatchRequest();
        patchRequest.setOp(this.op.getOp());
        patchRequest.setPath(this.path.getValue());
        patchRequest.setValue(this.value);
        return patchRequest;
    }
}
