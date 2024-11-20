package ua.wyverno.crowdin.api.storage.queries;

import com.crowdin.client.storage.StorageApi;
import com.crowdin.client.storage.model.Storage;
import ua.wyverno.crowdin.api.Query;

import java.io.InputStream;

public class StorageAddQuery implements Query<Storage> {

    private final StorageApi storageApi;
    private String fileName;
    private String contentStr;
    private InputStream contentInput;
    public StorageAddQuery(StorageApi storageApi) {
        this.storageApi = storageApi;
    }

    /**
     * @param fileName назва файлу
     * @return {@link StorageAddQuery}
     */
    public StorageAddQuery fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * @param content вміст файлу
     * @return {@link StorageAddQuery}
     */
    public StorageAddQuery content(String content) {
        this.contentStr = content;
        return this;
    }

    /**
     * @param content вміст файлу
     * @return {@link StorageAddQuery}
     */
    public StorageAddQuery content(InputStream content) {
        this.contentInput = content;
        return this;
    }

    @Override
    public Storage execute() {
        if (this.contentStr != null) return this.storageApi.addStorage(this.fileName, this.contentStr).getData();
        return this.storageApi.addStorage(this.fileName, this.contentInput).getData();
    }
}
