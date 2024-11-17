package ua.wyverno.crowdin;

import com.crowdin.client.Client;
import com.crowdin.client.core.model.Credentials;
import com.crowdin.client.core.model.ResponseList;
import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.config.Config;
import ua.wyverno.config.ConfigLoader;

import java.util.ArrayList;
import java.util.List;

@Service
public class CrowdinService {
    private final Client crowdinClient;
    private final SourceFilesApi sourceFilesApi;

    @Autowired
    public CrowdinService(ConfigLoader configLoader) {
        Config config = configLoader.getConfig();
        Credentials credentials = new Credentials(config.getToken(), null);
        this.crowdinClient = new Client(credentials);
        this.sourceFilesApi = this.crowdinClient.getSourceFilesApi();
    }

    public List<Directory> listAllRootDirectoriesFromProject(long projectID) {
        return listDirectoriesWithPagination(projectID, 50);
    }

    private List<Directory> listDirectoriesWithPagination(long projectID, int limit) {
        int offset = 0;
        List<Directory> allRootDirectories = new ArrayList<>();
        List<Directory> responseDirectories;
        do {
            responseDirectories = this.getDirectoriesFromAPI(projectID, null, null, null, null, limit, offset);
            // Додаємо дані про директорії до загального списку про директорії
            allRootDirectories.addAll(responseDirectories);
            // Оновлюємо offset враховуючи кількість повернутих директорій
            offset += responseDirectories.size();
            // Якщо кількість повернутих директорій менша за ліміт, то це означає, що більше директорій немає
        } while (responseDirectories.size() == limit);

        return allRootDirectories;
    }

    private List<Directory> getDirectoriesFromAPI(long projectID, Long branchID, Long directoryID, String filter, Object recursion, int limit, int offset) {
        ResponseList<Directory> response = this.sourceFilesApi
                .listDirectories(projectID, branchID, directoryID, filter, recursion, limit, offset);
        // Конвертуємо відповідь у лист з даними про директорії
        return response.getData()
                .stream()
                .map(ResponseObject::getData)
                .toList();
    }
}
