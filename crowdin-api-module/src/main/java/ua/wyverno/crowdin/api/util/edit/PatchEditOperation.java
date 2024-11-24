package ua.wyverno.crowdin.api.util.edit;

import com.crowdin.client.core.model.PatchOperation;

public enum PatchEditOperation {
    REPLACE(PatchOperation.REPLACE), TEST(PatchOperation.TEST);
    private final PatchOperation op;

    PatchEditOperation(PatchOperation op) {
        this.op = op;
    }

    public PatchOperation getOp() {
        return op;
    }
}
