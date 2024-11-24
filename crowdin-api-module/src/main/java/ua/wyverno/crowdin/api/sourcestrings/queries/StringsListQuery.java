package ua.wyverno.crowdin.api.sourcestrings.queries;

import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcestrings.SourceStringsApi;
import com.crowdin.client.sourcestrings.model.ListSourceStringsParams;
import com.crowdin.client.sourcestrings.model.SourceString;
import ua.wyverno.crowdin.api.sourcestrings.ListStringsQuery;

import java.util.List;

public class StringsListQuery extends ListStringsQuery<SourceString, StringsListQuery> {

    public StringsListQuery(SourceStringsApi sourceStringsApi, long projectID) {
        super(sourceStringsApi, projectID);
    }

    @Override
    public List<SourceString> execute() {
        return this.listWithPagination();
    }

    @Override
    protected List<SourceString> fetchFromAPI(int limitAPI, int offset) {
        ListSourceStringsParams params = ListSourceStringsParams.builder()
                .orderBy(this.getOrderBy())
                .denormalizePlaceholders(this.getDenormalizePlaceholders())
                .labelIds(this.getLabelsIDs())
                .fileId(this.getFileID())
                .directoryId(this.getDirectoryID())
                .croql(this.getCroQL())
                .filter(this.getFilterAPI())
                .scope(this.getScope())
                .limit(limitAPI)
                .offset(offset)
                .build();
        return this.getSourceStringsApi().listSourceStrings(this.getProjectID(), params).getData()
                .stream()
                .map(ResponseObject::getData)
                .toList();
    }
}
