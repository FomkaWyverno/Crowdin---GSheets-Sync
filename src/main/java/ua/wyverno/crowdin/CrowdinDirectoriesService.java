package ua.wyverno.crowdin;

import com.crowdin.client.sourcefiles.model.Directory;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.function.Predicate;

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
    List<Directory> findDirectories(long projectID, @NonNull Predicate<Directory> filter);
    /**
     * Шукає певні директорії за назвами директорій
     * @param projectID айді проєкта де потрібно шукати директорію
     * @param directoryID айді директорії де потрібно шукати директорії
     * @param directoriesNames список імен директорій які потрібно знайти за один пошук
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.Directory}> всі знайдені директорії. Список буде порожнім, якщо не було знайдено жодної директорії
     */
    List<Directory> findDirectories(long projectID, Long directoryID, @NonNull Predicate<Directory> filter);

    /**
     * Шукає певні директорії за назвами директорій
     * @param projectID айді проєкта де потрібно шукати директорію
     * @param directoryID айді директорії де потрібно шукати директорії
     * @param isRecursive рекурсивний пошук
     * @param directoriesNames список імен директорій які потрібно знайти за один пошук
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.Directory}> всі знайдені директорії. Список буде порожнім, якщо не було знайдено жодної директорії
     */
    List<Directory> findDirectories(long projectID, Long directoryID, boolean isRecursive, @NonNull Predicate<Directory> filter);
    /**
     * Шукає певні директорії за назвами директорій
     * @param projectID айді проєкта де потрібно шукати директорію
     * @param filter список імен директорій які потрібно знайти за один пошук
     * @param maxResults кількість директорій яку потрібно знайти
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.Directory}> всі знайдені директорії. Список буде порожнім, якщо не було знайдено жодної директорії
     */
    List<Directory> findDirectories(long projectID, @NonNull Predicate<Directory> filter, Integer countDirectory);
    /**
     * Шукає певні директорії за назвами директорій
     * @param projectID айді проєкта де потрібно шукати директорію
     * @param directoryID айді директорії де потрібно шукати директорії
     * @param filter список імен директорій які потрібно знайти за один пошук
     * @param maxResults кількість директорій яку потрібно знайти
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.Directory}> всі знайдені директорії. Список буде порожнім, якщо не було знайдено жодної директорії
     */
    List<Directory> findDirectories(long projectID, Long directoryID, @NonNull Predicate<Directory> filter, Integer maxResults);

    /**
     * Шукає певні директорії за назвами директорій
     * @param projectID айді проєкта де потрібно шукати директорію
     * @param directoryID айді директорії де потрібно шукати директорії
     * @param isRecursive рекурсивний пошук
     * @param filter список імен директорій які потрібно знайти за один пошук
     * @param maxResults кількість директорій яку потрібно знайти
     * @return {@link List}<{@link com.crowdin.client.sourcefiles.model.Directory}> всі знайдені директорії. Список буде порожнім, якщо не було знайдено жодної директорії
     */
    List<Directory> findDirectories(long projectID, Long directoryID, boolean isRecursive, @NonNull Predicate<Directory> filter, Integer maxResults);
    /**
     * Створює директорію у Кроудін
     * @param projectID айді проєкта де потрібно створити директорію
     * @param directoryName назва директорії
     * @return Інформація про створену директорію
     */
    Directory createDirectory(long projectID, String directoryName);

    /**
     * Створює директорію у Кроудін
     * @param projectID айді проєкта де потрібно створити директорію
     * @param directoryName назва директорії
     * @param directoryID айді директорії у якій потрібно створити директорії
     * @return Інформація про створену директорію
     */
    Directory createDirectory(long projectID, String directoryName, Long directoryID);
}
