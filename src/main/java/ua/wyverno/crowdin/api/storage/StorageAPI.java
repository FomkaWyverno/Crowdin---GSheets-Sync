package ua.wyverno.crowdin.api.storage;

import java.io.InputStream;

public interface StorageAPI {
    /**
     * Створює запит до Crowdin API - Add Storage
     * @param fileName назва файлу
     * @param content вміст файлу
     * @return {@link StorageAddQuery}
     */
    StorageAddQuery add(String fileName, String content);
    /**
     * Створює запит до Crowdin API - Add Storage
     * @param fileName назва файлу
     * @param content вміст файлу
     * @return {@link StorageAddQuery}
     */
    StorageAddQuery add(String fileName, InputStream content);

}
