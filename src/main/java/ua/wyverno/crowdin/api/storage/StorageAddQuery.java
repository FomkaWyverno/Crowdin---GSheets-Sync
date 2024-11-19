package ua.wyverno.crowdin.api.storage;

import com.crowdin.client.storage.StorageApi;
import com.crowdin.client.storage.model.Storage;
import ua.wyverno.crowdin.api.Query;

import java.io.InputStream;

public class StorageAddQuery implements Query<Storage> {

    private final StorageApi storageApi;
    private final String fileName;
    private final String contentStr;
    private final InputStream contentInput;
    public StorageAddQuery(StorageApi storageApi, String fileName, String content) {
        this.storageApi = storageApi;
        this.fileName = fileName;
        this.contentStr = content;
        this.contentInput = null;
    }

    public StorageAddQuery(StorageApi storageApi, String fileName, InputStream content) {
        this.storageApi = storageApi;
        this.fileName = fileName;
        this.contentInput = content;
        this.contentStr = null;
    }

    @Override
    public Storage execute() {
        if (this.contentStr != null) return this.storageApi.addStorage(this.fileName, this.contentStr).getData();
        return this.storageApi.addStorage(this.fileName, this.contentInput).getData();
    }
}
