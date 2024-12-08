package ua.wyverno.crowdin.api.storage.queries;

import com.crowdin.client.storage.StorageApi;
import com.crowdin.client.storage.model.Storage;
import ua.wyverno.crowdin.api.Query;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StorageAddQuery implements Query<Storage> {

    private final StorageApi storageApi;
    private String fileName;
    private InputStream content;
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
        this.content = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    /**
     * @param content вміст файлу
     * @return {@link StorageAddQuery}
     */
    public StorageAddQuery content(InputStream content) {
        this.content = content;
        return this;
    }

    @Override
    public Storage execute() {
        return this.storageApi.addStorage(this.fileName, this.content).getData();
    }
}
