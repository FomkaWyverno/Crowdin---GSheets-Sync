package ua.wyverno.crowdin.api.sourcefiles;

import com.crowdin.client.sourcefiles.SourceFilesApi;
import ua.wyverno.crowdin.api.ListQuery;

import java.util.List;


/**
 * Базовий клас для запитів SourceFiles, який збирає листи з елементами
 * @param <T> Генерик тип елементів листа які має повернути запит до АПІ
 * @param <Q> Основний клас запиту який має виконувати АПІ запит, і обов'язково має успадковувати клас {@link ListSourceFilesQuery},
 *           має бути обов'язково вказаний, щоб методи які повертають себе, за патерном Builder, повертали успадкований клас запиту, а не цей абстракний клас.
 */
public abstract class ListSourceFilesQuery<T, Q extends ListSourceFilesQuery<T,Q>> extends ListQuery<T,Q> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectID;
    private Long directoryID = null;
    private String filterApi = null;
    private boolean isRecursive = false;

    protected ListSourceFilesQuery(SourceFilesApi sourceFilesApi, long projectID) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectID = projectID;
    }

    /**
     * Пошук у певній директорії
     * @param directoryID айді директорії де потрібно шукати
     * @return {@link Q}
     */
    public Q directoryID(Long directoryID) {
        this.directoryID = directoryID;
        return this.self();
    }

    /**
     * Використовується для вказування для фільтра на боку АПІ.
     * Рекомендується використовувати саме цей метод, якщо вам потрібно шукати елементи лише за назвою
     * @param filterApi назва елементи
     * @return {@link Q}
     */
    public Q filterApi(String filterApi) {
        this.filterApi = filterApi;
        return this.self();
    }

    /**
     * Рекурсивний пошук на боку АПІ
     * @param isRecursive чи потрібно використовувати рекурсивний пошук
     * @return {@link Q}
     */
    public Q recursive(boolean isRecursive) {
        this.isRecursive = isRecursive;
        return this.self();
    }

    protected SourceFilesApi getSourceFilesApi() {
        return sourceFilesApi;
    }

    protected long getProjectID() {
        return projectID;
    }

    protected Long getDirectoryID() {
        return directoryID;
    }

    protected String getFilterApi() {
        return filterApi;
    }

    protected boolean isRecursive() {
        return isRecursive;
    }
}
