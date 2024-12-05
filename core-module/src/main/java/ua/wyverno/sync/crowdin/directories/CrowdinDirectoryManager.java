package ua.wyverno.sync.crowdin.directories;

import com.crowdin.client.sourcefiles.model.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.DirectoryEditQuery;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.edit.PatchDirRequestBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Component
public class CrowdinDirectoryManager {

    private final CrowdinService crowdinService;
    private final long projectId;

    @Autowired
    public CrowdinDirectoryManager(CrowdinService crowdinService, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.projectId = configLoader.getCoreConfig().getProjectID();
    }

    /**
     * Шукає директорії за списком імен директорій
     * @param directoriesNames імена директорій які потрібно знайти
     * @param directoryId айді директорії у якій знаходяться директорії які потрібно знайти, null якщо це корні проєкта має лежати
     * @return Список директорій у Кроудіні
     */
    protected List<Directory> getDirectoriesByNames(List<String> directoriesNames, Long directoryId) {
        Objects.requireNonNull(directoriesNames, "filesNames can't be null!");
        if (directoriesNames.isEmpty()) throw new IllegalArgumentException("directoriesNames can't be empty!");
        return this.crowdinService.directories()
                .list(this.projectId)
                .filter(directory -> directoriesNames.contains(directory.getName()))
                .execute();
    }

    /**
     * Шукає директорії за списком шляхів директорій
     * @param paths шляхи до директорії
     * @param directoryId айді директорії у якій знаходяться директорії які потрібно знайти, null якщо це у корні проєкта має лежати
     * @return Список директорії у Кроудіні
     */
    protected List<Directory> getDirectoriesByPaths(List<String> paths, Long directoryId) {
        Objects.requireNonNull(paths, "paths can't be null!");
        if (paths.isEmpty()) throw new IllegalArgumentException("paths can't be empty!");
        return this.crowdinService.directories()
                .list(this.projectId)
                .maxResults(paths.size())
                .filter(directory -> paths.contains(directory.getPath()))
                .execute();
    }
    /**
     * Шукає одну директорію за іменем, цей метод може повернути не очікуваний результат, через те, що метод<br/>
     * Може повернути директорію не з тієї директорії якої ви очікували, а її під директорії, якщо не було знайдено<br/>
     * Конкретно в директорії яку було вказано у методі, буде шукати в під директорії.
     * @param directoryName ім'я директорії
     * @param directoryId айді директорії де знаходиться директорія яку шукаємо, null якщо ця директорія у корні проєкта має лежати
     * @return Директорію з Кроудіна, якщо не знайдено поверне null
     */
    protected Directory getDirectoryByName(String directoryName, @Nullable Long directoryId) {
        Objects.requireNonNull(directoryName, "DirectoryName can't be null!");
        if (directoryName.isEmpty()) throw new IllegalArgumentException("DirectoryName can't be empty!");
        List<Directory> directories = this.crowdinService.directories()
                .list(this.projectId)
                .maxResults(1)
                .limitAPI(1)
                .directoryID(directoryId)
                .filterApi(directoryName)
                .execute();
        if (directories.isEmpty()) return null;
        return directories.get(0);
    }

    /**
     * Шукає одну директорію за шляхом до неї<br/>
     * Цей метод є більш точним, але не дуже ефективним стосовно використання АПІ,<br/>
     * Через те, що можна 100% бути впевненим,<br/>
     * Якщо вказати path /main, то це 100% буде лежати у корні,<br/>
     * Коли при використанні {@link CrowdinDirectoryManager#getDirectoryByName(String, Long)}<br/>
     * Може повернути директорію, якщо не було знайдено у кореневій директорії, але було знайдено у під директорії,<br/>
     * І це може бути не очікуваний результат<br/>
     * Цей же метод якщо вам потрібно лише у кореневій директорії, він 100% поверне з кореневої директорії
     * @param path шлях
     * @param directoryId айді директорії де знаходиться директорія яку шукаємо, null якщо ця директорія у корні проєкта має лежати
     * @return Директорію з Кроудіна, якщо не знайдено поверне null
     */
    public Directory getDirectoryByPath(String path, @Nullable Long directoryId) {
        Objects.requireNonNull(path, "Path can't be null!");
        if (path.isEmpty()) throw new IllegalArgumentException("Path can't be empty!");
        Path pathToFile = Paths.get(path);
        List<Directory> directories = this.crowdinService.directories()
                .list(this.projectId)
                .directoryID(directoryId)
                .maxResults(1)
                .limitAPI(1)
                .filterApi(pathToFile.getFileName().toString())
                .filter(directory -> directory.getPath().equals(path))
                .execute();

        if (directories.isEmpty()) return null;
        if (directories.size() > 1) {throw new AmbiguousDirectoryException(String.format("Multiple directories found for path %s. Expected only one.", path));}
        return directories.get(0);
    }
    /**
     * Створює директорію у Кроудіні
     * @param directoryId айді директорії де має бути розташована директорія, якщо це коренева директорія має бути null
     * @param directoryName ім'я директорії
     * @param directoryTitle заголовок директорії може бути null, щоб не встановлювати загаловок
     * @return {@link Directory} створена директорія на Кроудіні
     */
    protected Directory createDirectory(@Nullable Long directoryId, String directoryName, @Nullable String directoryTitle) {
        Objects.requireNonNull(directoryName, "Directory name can't be null!");
        return this.crowdinService.directories() // Створюємо директорію у Кроудіні
                .createDirectory(this.projectId)
                .directoryID(directoryId)
                .name(directoryName)
                .title(directoryTitle)
                .execute();
    }

    protected Directory editDirectory(Long directoryId, List<PatchDirRequestBuilder> patchDirRequests) {
        DirectoryEditQuery query = this.crowdinService.directories()
                .editDirectory(this.projectId)
                .directoryID(directoryId);
        patchDirRequests.forEach(query::addPatchRequest);
        return query.execute();
    }
}
