package ua.wyverno.crowdin.api.source.files;

import com.crowdin.client.core.model.ResponseList;
import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DirectoriesAPI {

    private final SourceFilesApi sourceFilesApi;

    @Autowired
    public DirectoriesAPI(CrowdinApiClient crowdinApiClient) {
        this.sourceFilesApi = crowdinApiClient.getCrowdinClient().getSourceFilesApi();
    }

    public List<Directory> listDirectoriesWithPagination(long projectID, int limit, Long directoryID, boolean isRecursive) {
        int offset = 0;
        List<Directory> allDirectories = new ArrayList<>();
        List<Directory> responseDirectories;
        do {
            responseDirectories = this.getDirectoriesFromAPI(projectID, null, directoryID, null, isRecursive, limit, offset);
            // Додаємо дані про директорії до загального списку про директорії
            allDirectories.addAll(responseDirectories);
            // Оновлюємо offset враховуючи кількість повернутих директорій
            offset += responseDirectories.size();
            // Якщо кількість повернутих директорій менша за ліміт, то це означає, що більше директорій немає
        } while (responseDirectories.size() == limit);

        return allDirectories;
    }

    public List<Directory> findDirectories(long projectID, int limit, Long directoryID, boolean isRecursive, List<String> directoriesNames) {
        if (directoriesNames.isEmpty()) return Collections.emptyList(); // Якщо лист з іменами які потрібно знайти порожній, повертаємо одразу порожній лист.
        return this.listDirectoriesWithPagination(projectID, limit, directoryID, isRecursive)
                .stream()
                .filter(directory -> directoriesNames.contains(directory.getName()))
                .toList();
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
