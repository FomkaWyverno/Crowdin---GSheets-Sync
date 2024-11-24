package ua.wyverno.crowdin.api.sourcestrings.queries.builders;

import com.crowdin.client.core.model.PatchOperation;
import com.crowdin.client.core.model.PatchRequest;

public class RemoveBatchStringRequestBuilder {
    private Long stringID;

    public RemoveBatchStringRequestBuilder stringID(Long stringID) {
        this.stringID = stringID;
        return this;
    }

    public PatchRequest build() {
        PatchRequest request = new PatchRequest();
        request.setOp(PatchOperation.REMOVE);
        request.setPath("/"+this.stringID);

        return request;
    }
}
