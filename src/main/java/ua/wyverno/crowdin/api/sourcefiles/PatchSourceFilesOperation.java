package ua.wyverno.crowdin.api.sourcefiles;

import com.crowdin.client.core.model.PatchOperation;

public enum PatchSourceFilesOperation {
    REPLACE(PatchOperation.REPLACE), TEST(PatchOperation.TEST);
    private final PatchOperation op;

    PatchSourceFilesOperation(PatchOperation op) {
        this.op = op;
    }

    public PatchOperation getOp() {
        return op;
    }
}
