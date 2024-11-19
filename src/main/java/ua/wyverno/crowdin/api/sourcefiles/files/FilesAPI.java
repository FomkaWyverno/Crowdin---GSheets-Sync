package ua.wyverno.crowdin.api.sourcefiles.files;

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
     * Створює файл у проєкті
     * @param projectID айді проєкта де потрібно створити файл
     * @param storageID айді вміста
     * @param name назва файлу
     * @return {@link FilesListQuery}
     */
    FilesCreateQuery create(long projectID, long storageID, String name);
}
