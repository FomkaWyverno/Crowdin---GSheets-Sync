package ua.wyverno.crowdin.api.sourcefiles.files;

import ua.wyverno.crowdin.api.sourcefiles.files.queries.FilesCreateQuery;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.FilesDeleteQuery;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.FilesEditQuery;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.FilesListQuery;

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
     * Після успішного запиту створює файл на проєкті
     * @param projectID айді проєкта де потрібно створити файл
     * @param storageID айді вміста
     * @param name назва файлу
     * @return {@link FilesListQuery}
     */
    FilesCreateQuery create(long projectID, long storageID, String name);

    /**
     * Створює запит до Crowdin API - Edit File
     * Редагує файл
     * @param projectID айді проєкта де знаходиться файл
     * @param fileID файл айді потрібно редагувати
     * @return {@link FilesEditQuery}
     */
    FilesEditQuery edit(long projectID, long fileID);

    /**
     * Створює запит до Crowdin API - Delete File Видаляє гру
     * @param projectID айді проєкта де знаходиться файл
     * @param fileID айді файла який потрібно видалити
     * @return {@link FilesDeleteQuery}
     */
    FilesDeleteQuery delete(long projectID, long fileID);
}
