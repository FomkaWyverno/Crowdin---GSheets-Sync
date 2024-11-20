package ua.wyverno.crowdin.api.storage;

import com.crowdin.client.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;
import ua.wyverno.crowdin.api.storage.queries.StorageAddQuery;

import java.io.InputStream;

@Component
public class StorageApiImpl implements StorageAPI {
    private final StorageApi storageApi;

    @Autowired
    public StorageApiImpl(CrowdinApiClient crowdinApiClient) {
        this.storageApi = crowdinApiClient.getCrowdinClient().getStorageApi();
    }


    @Override
    public StorageAddQuery add(String fileName, String content) {
        return new StorageAddQuery(this.storageApi, fileName, content);
    }

    @Override
    public StorageAddQuery add(String fileName, InputStream content) {
        return new StorageAddQuery(this.storageApi, fileName, content);
    }
}
