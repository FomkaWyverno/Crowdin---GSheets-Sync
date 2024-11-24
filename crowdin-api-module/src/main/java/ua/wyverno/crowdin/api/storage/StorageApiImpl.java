package ua.wyverno.crowdin.api.storage;

import com.crowdin.client.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;
import ua.wyverno.crowdin.api.storage.queries.StorageAddQuery;

@Component
public class StorageApiImpl implements StorageAPI {
    private final StorageApi storageApi;

    @Autowired
    public StorageApiImpl(CrowdinApiClient crowdinApiClient) {
        this.storageApi = crowdinApiClient.getCrowdinClient().getStorageApi();
    }
    @Override
    public StorageAddQuery add() {
        return new StorageAddQuery(this.storageApi);
    }
}
