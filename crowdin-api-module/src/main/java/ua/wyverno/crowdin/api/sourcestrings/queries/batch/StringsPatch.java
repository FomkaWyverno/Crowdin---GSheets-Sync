package ua.wyverno.crowdin.api.sourcestrings.queries.batch;

import com.crowdin.client.core.model.PatchOperation;
import com.crowdin.client.core.model.PatchRequest;

public abstract class StringsPatch {
    private final PatchOperation op;

    protected StringsPatch(PatchOperation op) {
        this.op = op;
    }

    protected PatchOperation getOp() {return op;}
    protected abstract String getPath();
    protected abstract Object getValue();


    public PatchRequest getPatchRequest() {
        PatchRequest patchRequest = new PatchRequest();
        patchRequest.setOp(this.getOp());
        patchRequest.setPath(this.getPath());
        patchRequest.setValue(this.getValue());
        return patchRequest;
    }
}
