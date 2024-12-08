package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.sourcestrings.model.SourceString;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.ReplaceBatchStringRequestBuilder;

import java.util.List;

public record SyncSheetSourceStringsResult(List<SourceString> existsStrings,
                                           List<AddStringRequestBuilder> preparedAddStringRequests,
                                           List<ReplaceBatchStringRequestBuilder> preparedReplaceStringRequest) { }
