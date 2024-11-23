package ua.wyverno.crowdin.api.storage;

import ua.wyverno.crowdin.api.storage.queries.StorageAddQuery;

import java.io.InputStream;

public interface StorageAPI {
    /**
     * Створює запит до Crowdin API - Add Storage<br/><br/>
     * Обов'язкові параметри при створенні запиту -<br/>
     * {@link StorageAddQuery#fileName(String fileName)} - назва файлу<br/>
     * {@link StorageAddQuery#content(String content)} - вміст файлу<br/><br/>
     * @return {@link StorageAddQuery}
     */
    StorageAddQuery add();

}
