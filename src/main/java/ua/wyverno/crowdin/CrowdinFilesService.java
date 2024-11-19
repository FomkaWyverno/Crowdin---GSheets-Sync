package ua.wyverno.crowdin;

import com.crowdin.client.sourcefiles.model.FileInfo;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.function.Predicate;

public interface CrowdinFilesService {
    /**
     * Повертає всі файли у проєкті
     * @param projectID айді проєкта
     * @return Лист з файлами
     */
    List<FileInfo> listAllFiles(long projectID);

    /**
     * Повертає всі файли у певній директорії без рекурсії
     * @param projectID айді проєкта
     * @param directoryID айді директорії
     * @return Лист з усіма файлами у певній директорії
     */
    List<FileInfo> listAllFiles(long projectID, Long directoryID);
    /**
     * Шукає певні файли за назвами файлів
     * @param projectID айді проєкта де потрібно шукати файли
     * @param filter фільтр для пошуку
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.FileInfo}> всі знайдені файли. Список буде порожнім, якщо не було знайдено жодного файлу
     */
    List<FileInfo> findFiles(long projectID, @NonNull Predicate<FileInfo> filter);
    /**
     * Шукає певні файли за назвами файлів
     * @param projectID айді проєкта де потрібно шукати файли
     * @param filter фільтр для пошуку
     * @param maxResults кількість файлів скільки потрібно знайти
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.FileInfo}> всі знайдені файли. Список буде порожнім, якщо не було знайдено жодного файлу
     */
    List<FileInfo> findFiles(long projectID, @NonNull Predicate<FileInfo> filter, Integer maxResults);
    /**
     * Шукає певні файли за назвами файлів
     * @param projectID айді проєкта де потрібно шукати файли
     * @param directoryID айді директорії де потрібно шукати файли
     * @param filter фільтр для пошуку
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.FileInfo}> всі знайдені файли. Список буде порожнім, якщо не було знайдено жодного файлу
     */
    List<FileInfo> findFiles(long projectID, Long directoryID, @NonNull Predicate<FileInfo> filter);
    /**
     * Шукає певні файли за назвами файлів
     * @param projectID айді проєкта де потрібно шукати файли
     * @param directoryID айді директорії де потрібно шукати файли
     * @param filter фільтр для пошуку
     * @param maxResults кількість файлів яких потрібно знайти
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.FileInfo}> всі знайдені файли. Список буде порожнім, якщо не було знайдено жодного файлу
     */
    List<FileInfo> findFiles(long projectID, Long directoryID, @NonNull Predicate<FileInfo> filter, Integer maxResults);
}
