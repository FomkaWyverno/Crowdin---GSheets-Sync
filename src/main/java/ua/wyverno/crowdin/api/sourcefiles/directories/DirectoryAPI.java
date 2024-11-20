package ua.wyverno.crowdin.api.sourcefiles.directories;

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
     * Щоб виконати запит викличте метод execute()<br/>
     * Ці параметри необхідні для запиту, тому вони мають бути вказані у цьому методі
     * @param projectID айді проєкта де потрібно створити директорію
     * @param name назва для створеної директорії
     * @return {@link DirectoryCreateQuery}
     */
    DirectoryCreateQuery createDirectory(long projectID, String name);

    /**
     * Створює запит до Crowdin API - Edit Directory
     * @param projectID айді проєкта де знаходиться директорія
     * @param directoryID айді директорії яку потрібно змінити
     * @return {@link DirectoryEditQuery}
     */
    DirectoryEditQuery editDirectory(long projectID, long directoryID);

    /**
     * Створює запит до Crowdin API - Delete Directories
     * @param projectID айді проєкта де потрібно видалити директорію
     * @param directoryID айді директорії яку потрібно видалити
     * @return {@link  DirectoryDeleteQuery}
     */
    DirectoryDeleteQuery deleteDirectory(long projectID, long directoryID);
}
