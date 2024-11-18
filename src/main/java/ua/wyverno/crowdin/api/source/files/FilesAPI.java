package ua.wyverno.crowdin.api.source.files;

import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.File;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilesAPI {
    private final SourceFilesApi sourceFilesApi;

    @Autowired
    public FilesAPI(CrowdinApiClient crowdinApiClient) {
        this.sourceFilesApi = crowdinApiClient.getCrowdinClient().getSourceFilesApi();
    }


    public List<FileInfo> listFilesWithPagination(long projectID, int limit, Long directoryID) {
        int offset = 0;
        List<FileInfo> allFilesInfo = new ArrayList<>();
        List<FileInfo> responseFilesInfo;
        do {
            responseFilesInfo = this.getListFilesFromAPI(projectID, null, directoryID, null, false, limit, offset);
            // Додаємо дані про файли до загального списку про файли
            allFilesInfo.addAll(responseFilesInfo);
            // Оновлюємо offset враховуючи кількість повернутих файлів
            offset += responseFilesInfo.size();
            // Якщо кількість повернутих файлів менша за ліміт, то це означає, що більше файлів немає
        } while (responseFilesInfo.size() == limit);

        return allFilesInfo;
    }

    public List<FileInfo> findFiles(long projectID, int limit, Long directoryID, List<String> filesNames) {
        if (filesNames.isEmpty()) return Collections.emptyList();
        return this.listFilesWithPagination(projectID, limit, directoryID)
                .stream()
                .filter(file -> filesNames.contains(file.getName()))
                .toList();
    }

    private List<FileInfo> getListFilesFromAPI(long projectID, Long branchID, Long directoryID, String filter, boolean isRecursion, int limit, int offset) {
        return this.sourceFilesApi.listFiles(projectID, branchID, directoryID, filter, isRecursion ? new Object() : null, limit, offset)
                .getData()
                .stream()
                .map(ResponseObject::getData)
                .collect(Collectors.toList());
    }

}
