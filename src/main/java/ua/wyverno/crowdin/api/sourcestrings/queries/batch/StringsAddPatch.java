package ua.wyverno.crowdin.api.sourcestrings.queries.batch;

import com.crowdin.client.core.model.PatchOperation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;

public class StringsAddPatch extends StringsPatch {

    private AddStringRequestBuilder requestBuilder;


    public StringsAddPatch(AddStringRequestBuilder addStringRequestBuilder) {
        super(PatchOperation.ADD);
        this.requestBuilder = addStringRequestBuilder;
    }

    @Override
    protected String getPath() {
        return "/-";
    }

    @Override
    protected Object getValue() {
        return this.requestBuilder.build();
    }
}
