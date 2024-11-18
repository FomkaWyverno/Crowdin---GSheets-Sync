package ua.wyverno.crowdin;

import com.crowdin.client.sourcefiles.model.Directory;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CrowdinDirectoriesService {

    /**
     * Повертає всі директорії у проєкті
     * @param projectID айді проєкта
     * @return Лист з директоріями
     */
    List<Directory> listAllDirectories(long projectID);

    /**
     * Повертає всі директорії у певній директорії без рекурсії
     * @param projectID айді проєкта
     * @param directoryID айді директорії
     * @return Лист з усіма директоріями у певній директорії
     */
    List<Directory> listAllDirectories(long projectID, Long directoryID);

    /**
     * Повертає всі директорії у певній директорії без рекурсії
     * @param projectID айді проєкта
     * @param directoryID айді директорії
     * @param isRecursive чи потрібно використовувати рекурсії у АПІ
     * @return Повертає всі директорії у певній директорії, з вибором на рекурсію
     */
    List<Directory> listAllDirectories(long projectID, Long directoryID, boolean isRecursive);

    /**
     * Шукає певні директорії за назвами директорій
     * @param projectID айді проєкта де потрібно шукати директорію
     * @param directoriesNames список імен директорій які потрібно знайти за один пошук
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.Directory}> всі знайдені директорії. Список буде порожнім, якщо не було знайдено жодної директорії
     */
    List<Directory> findDirectories(long projectID, @NonNull List<String> directoriesNames);
    /**
     * Шукає певні директорії за назвами директорій
     * @param projectID айді проєкта де потрібно шукати директорію
     * @param directoryID айді директорії де потрібно шукати директорії
     * @param directoriesNames список імен директорій які потрібно знайти за один пошук
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.Directory}> всі знайдені директорії. Список буде порожнім, якщо не було знайдено жодної директорії
     */
    List<Directory> findDirectories(long projectID, Long directoryID, @NonNull List<String> directoriesNames);

    /**
     * Шукає певні директорії за назвами директорій
     * @param projectID айді проєкта де потрібно шукати директорію
     * @param directoryID айді директорії де потрібно шукати директорії
     * @param isRecursive рекурсивний пошук
     * @param directoriesNames список імен директорій які потрібно знайти за один пошук
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.Directory}> всі знайдені директорії. Список буде порожнім, якщо не було знайдено жодної директорії
     */
    List<Directory> findDirectories(long projectID, Long directoryID, boolean isRecursive, @NonNull List<String> directoriesNames);
}
