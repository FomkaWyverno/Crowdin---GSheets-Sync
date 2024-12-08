package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.sourcestrings.model.SourceString;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;
import java.util.List;

public record SyncSheetSourceStringsResult(List<SourceString> existsStrings,
                                           List<AddStringRequestBuilder> preparedAddStringRequests) { }
