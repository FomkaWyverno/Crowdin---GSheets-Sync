package ua.wyverno.crowdin.api.sourcefiles.directories.queries.edit;

import com.crowdin.client.core.model.PatchOperation;
import com.crowdin.client.core.model.PatchRequest;
import ua.wyverno.crowdin.api.sourcefiles.PatchSourceFilesOperation;

public class PatchDirRequestBuilder {
    private PatchOperation op;
    private EditDirPath path;
    private Object value;

    public PatchDirRequestBuilder op(PatchSourceFilesOperation patchDirOperation) {
        this.op = patchDirOperation.getOp();
        return this;
    }

    public PatchDirRequestBuilder path(EditDirPath path) {
        this.path = path;
        return this;
    }

    public PatchDirRequestBuilder value(String value) {
        this.value = value;
        return this;
    }
    public PatchDirRequestBuilder value(Integer value) {
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
