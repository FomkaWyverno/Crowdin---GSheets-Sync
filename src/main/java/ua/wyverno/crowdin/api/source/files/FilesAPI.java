package ua.wyverno.crowdin.api.source.files;

import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.File;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class FilesAPI {
    private final SourceFilesApi sourceFilesApi;

    @Autowired
    public FilesAPI(CrowdinApiClient crowdinApiClient) {
        this.sourceFilesApi = crowdinApiClient.getCrowdinClient().getSourceFilesApi();
    }


    public List<FileInfo> listFilesWithPagination(long projectID, int limit, Long directoryID) {
        return this.listFilesWithPagination(projectID, limit, directoryID, null, null);
    }

    public List<FileInfo> findFiles(long projectID, int limit, Long directoryID, Predicate<FileInfo> filter, Integer maxResults) {
        return this.listFilesWithPagination(projectID, limit, directoryID, filter, maxResults);
    }

    private List<FileInfo> listFilesWithPagination(long projectID, int limit, Long directoryID,
                                                   @Nullable Predicate<FileInfo> filter, @Nullable Integer maxResults) {
        int offset = 0;
        List<FileInfo> resultFiles = new ArrayList<>();
        List<FileInfo> responseFilesInfo;
        do {
            responseFilesInfo = this.getListFilesFromAPI(projectID, null, directoryID, null, false, limit, offset);
            // Додаємо дані про файли до загального списку про файли
            if (filter != null) { // Якщо є фільтр додаємо відфільтровані дані
                if (maxResults != null) { // Якщо є максимальна кількість результатів
                    int remainingSpace = maxResults - resultFiles.size();
                    resultFiles.addAll(responseFilesInfo.stream().filter(filter).limit(remainingSpace).toList());
                } else {
                    resultFiles.addAll(responseFilesInfo.stream().filter(filter).toList());
                }
            } else {
                resultFiles.addAll(responseFilesInfo);
            }
            // Оновлюємо offset враховуючи кількість повернутих файлів
            offset += responseFilesInfo.size();
            // Якщо кількість повернутих файлів менша за ліміт, то це означає, що більше файлів немає
        } while (responseFilesInfo.size() == limit && (maxResults == null || resultFiles.size() >= maxResults));

        return resultFiles;
    }
    private List<FileInfo> getListFilesFromAPI(long projectID, Long branchID, Long directoryID, String filter, boolean isRecursion, int limit, int offset) {
        return this.sourceFilesApi.listFiles(projectID, branchID, directoryID, filter, isRecursion ? new Object() : null, limit, offset)
                .getData()
                .stream()
                .map(ResponseObject::getData)
                .collect(Collectors.toList());
    }

}
