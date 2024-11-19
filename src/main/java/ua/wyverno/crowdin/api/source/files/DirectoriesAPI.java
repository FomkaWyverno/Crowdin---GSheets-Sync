package ua.wyverno.crowdin.api.source.files;

import com.crowdin.client.core.model.ResponseList;
import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.AddDirectoryRequest;
import com.crowdin.client.sourcefiles.model.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component
public class DirectoriesAPI {

    private final SourceFilesApi sourceFilesApi;

    @Autowired
    public DirectoriesAPI(CrowdinApiClient crowdinApiClient) {
        this.sourceFilesApi = crowdinApiClient.getCrowdinClient().getSourceFilesApi();
    }
    public List<Directory> listDirectoriesWithPagination(long projectID, int limit, Long directoryID, boolean isRecursive) {
        return this.listDirectoriesWithPagination(projectID, limit, directoryID, isRecursive, null, null);
    }

    public List<Directory> findDirectories(long projectID, int limit, Long directoryID, boolean isRecursive, Predicate<Directory> filter, Integer maxResults) {
        return this.listDirectoriesWithPagination(projectID, limit, directoryID, isRecursive, filter, maxResults);
    }

    public Directory createDirectory(long projectID, String directoryName, Long directoryID) {
        AddDirectoryRequest addDirectoryRequest = new AddDirectoryRequest();
        addDirectoryRequest.setName(directoryName);
        addDirectoryRequest.setDirectoryId(directoryID);
        return this.sourceFilesApi.addDirectory(projectID, addDirectoryRequest).getData();
    }

    private List<Directory> listDirectoriesWithPagination(long projectID, int limit, Long directoryID, boolean isRecursive,
                                                          @Nullable Predicate<Directory> filter, @Nullable Integer maxResults) {
        int offset = 0;
        List<Directory> resultDirectories = new ArrayList<>();
        List<Directory> responseDirectories;
        do {
            responseDirectories = this.getDirectoriesFromAPI(projectID, null, directoryID, null, isRecursive, limit, offset);
            // Додаємо дані про директорії до загального списку про директорії
            if (filter != null) { // Якщо є фільтр додаємо відфільтровані дані
                if (maxResults != null) {
                    int remainingSpace = maxResults - resultDirectories.size();
                    resultDirectories.addAll(responseDirectories.stream().filter(filter).limit(remainingSpace).toList());
                } else {
                    resultDirectories.addAll(responseDirectories.stream().filter(filter).toList());
                }
            } else {
                resultDirectories.addAll(responseDirectories);
            }
            // Оновлюємо offset враховуючи кількість повернутих директорій
            offset += responseDirectories.size();
            // Якщо кількість повернутих директорій менша за ліміт, то це означає, що більше директорій немає
            // Та якщо є встановлений ліміт на кількість результатів, завершуємо після отриманні потрібної кількості
        } while (responseDirectories.size() == limit && (maxResults == null || resultDirectories.size() >= maxResults));

        return resultDirectories;
    }

    private List<Directory> getDirectoriesFromAPI(long projectID, Long branchID, Long directoryID, String filter, boolean isRecursion, int limit, int offset) {
        ResponseList<Directory> response = this.sourceFilesApi
                .listDirectories(projectID, branchID, directoryID, filter, isRecursion ? new Object() : null, limit, offset);
        // Конвертуємо відповідь у лист з даними про директорії
        return response.getData()
                .stream()
                .map(ResponseObject::getData)
                .toList();
    }
}
