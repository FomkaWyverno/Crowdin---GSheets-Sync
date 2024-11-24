package ua.wyverno.crowdin.api.sourcefiles.directories;

import ua.wyverno.crowdin.api.sourcefiles.directories.queries.DirectoryCreateQuery;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.DirectoryDeleteQuery;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.DirectoryEditQuery;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.DirectoryListQuery;

public interface DirectoryAPI {
    /**
     * Створює запит до Crowdin API - List Directories<br/>
     * Щоб виконати запит викличте метод execute()<br/>
     * Ці параметри необхідні для запиту, тому вони мають бути вказані у цьому методі
     * @param projectID айді проєкта де потрібно отримати лист з директоріями
     * @return {@link DirectoryListQuery}
     */
    DirectoryListQuery list(long projectID);
    /**
     * Створює запит до Crowdin API - Add Directory<br/>
     * Щоб виконати запит викличте метод execute()<br/><br/>
     * Обов'язкові параметри при створенні запиту -<br/>
     * {@link DirectoryCreateQuery#name(String name)}<br/><br/>
     * @param projectID айді проєкта де потрібно створити директорію
     * @return {@link DirectoryCreateQuery}
     */
    DirectoryCreateQuery createDirectory(long projectID);

    /**
     * Створює запит до Crowdin API - Edit Directory<br/><br/>
     * Обов'язкові параметри при створенні запиту -<br/>
     * {@link DirectoryEditQuery#directoryID(long directoryID)} - айді директорії яку потрібно змінити<br/><br/>
     * @param projectID айді проєкта де знаходиться директорія
     * @return {@link DirectoryEditQuery}
     */
    DirectoryEditQuery editDirectory(long projectID);

    /**
     * Створює запит до Crowdin API - Delete Directories<br/><br/>
     * Обов'язкові параметри при створенні запиту -<br/>
     * {@link DirectoryDeleteQuery#directoryID(long directoryID)} айді директорії яку потрібно видалити<br/><br/>
     * @param projectID айді проєкта де потрібно видалити директорію
     * @return {@link  DirectoryDeleteQuery}
     */
    DirectoryDeleteQuery deleteDirectory(long projectID);
}
