package ua.wyverno.crowdin.api.sourcefiles.directories;

public interface DirectoryAPI {
    /**
     * Створює запит до Crowdin API до List Directories<br/>
     * Щоб виконати запит викличте метод execute()<br/>
     * Ці параметри необхідні для запиту, тому вони мають бути вказані у цьому методі
     * @param projectID айді проєкта де потрібно отримати лист з директоріями
     * @return {@link DirectoryListQuery}
     */
    DirectoryListQuery list(long projectID);
    /**
     * Створює запит до Crowdin API до Add Directory<br/>
     * Щоб виконати запит викличте метод execute()<br/>
     * Ці параметри необхідні для запиту, тому вони мають бути вказані у цьому методі
     * @param projectID айді проєкта де потрібно створити директорію
     * @param name назва для створеної директорії
     * @return {@link DirectoryCreateQuery}
     */
    DirectoryCreateQuery createDirectory(long projectID, String name);
}
