package ua.wyverno.crowdin.api.sourcefiles.files;

import ua.wyverno.crowdin.api.sourcefiles.files.queries.*;

public interface FilesAPI {
    /**
     * Створює запит до Crowdin API до List Files<br/>
     * Щоб виконати запит викличте метод execute()<br/>
     * Ці параметри необхідні для запиту, тому вони мають бути вказані у цьому методі
     * @param projectID айді проєкта де потрібно отримати лист з файлами
     * @return {@link FilesListQuery}
     */
    FilesListQuery list(long projectID);

    /**
     * Створює запит до Crowdin API - Add Files<br/>
     * Після успішного запиту створює файл на проєкті<br/><br/>
     * Обов'язкові параметри при створенні запиту -<br/>
     * {@link FilesCreateQuery#storageID(long storageID)}<br/>
     * {@link FilesCreateQuery#name(String name)}<br/><br/>
     * @param projectID айді проєкта де потрібно створити файл
     * @return {@link FilesListQuery}
     */
    FilesCreateQuery create(long projectID);

    /**
     * Створює запит до Crowdin API - Edit File<br/>
     * Редагує файл<br/><br/>
     * Обов'язкові параметри при створенні запиту -<br/>
     * {@link FilesEditQuery#fileID(long fileID)}<br/><br/>
     * @param projectID айді проєкта де знаходиться файл
     * @return {@link FilesEditQuery}
     */
    FilesEditQuery edit(long projectID);

    /**
     * Створює запит до Crowdin API - Delete File Видаляє гру<br/><br/>
     * Обов'язкові параметри при створенні запиту -<br/>
     * {@link FilesEditQuery#fileID(long fileID)}<br/><br/>
     * @param projectID айді проєкта де знаходиться файл
     * @return {@link FilesDeleteQuery}
     */
    FilesDeleteQuery delete(long projectID);

    /**
     * Створює запит до Crowdin API - Update or Restore File<br/><br/>
     * Обов'язкові параметри при створенні запиту -<br/>
     * @param projectId айді проєкта де знаходиться файл
     * @return {@link FilesUpdateOrRestoreQuery}
     */
    FilesUpdateOrRestoreQuery updateOrRestore(long projectId);
}
