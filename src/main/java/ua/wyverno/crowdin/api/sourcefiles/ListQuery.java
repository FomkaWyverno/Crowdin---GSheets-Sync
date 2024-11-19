package ua.wyverno.crowdin.api.sourcefiles;

import com.crowdin.client.sourcefiles.SourceFilesApi;
import ua.wyverno.crowdin.api.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class ListQuery<T> implements Query<List<T>> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectID;
    private int limitApi = 100;
    private int offsetApi = 0;
    private Long directoryID = null;
    private String filterApi = null;
    private boolean isRecursive = false;
    private boolean pagination = true;
    private Predicate<T> filter;
    private Integer maxResults = null;

    protected ListQuery(SourceFilesApi sourceFilesApi, long projectID) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectID = projectID;
    }

    /**
     * Обмежити кількість отриманих з АПІ елементів від 0 до 100
     * @param limit кількість елементів
     * @return {@link ListQuery<T>}
     */
    public ListQuery<T> limitAPI(int limit) {
        this.limitApi = limit;
        return this;
    }

    /**
     * Вказує з якого елемента потрібно збирати елементи
     * @param offset індифікатор з якого елемента потрібно почати пошук
     * @return {@link ListQuery<T>}
     */
    public ListQuery<T> offset(int offset) {
        this.offsetApi = offset;
        return this;
    }

    /**
     * Пошук у певній директорії
     * @param directoryID айді директорії де потрібно шукати
     * @return {@link ListQuery<T>}
     */
    public ListQuery<T> directoryID(Long directoryID) {
        this.directoryID = directoryID;
        return this;
    }

    /**
     * Використовується для вказування для фільтра на боку АПІ.
     * Рекомендується використовувати саме цей метод, якщо вам потрібно шукати елементи лише за назвою
     * @param filterApi назва елементи
     * @return {@link ListQuery<T>}
     */
    public ListQuery<T> filterApi(String filterApi) {
        this.filterApi = filterApi;
        return this;
    }

    /**
     * Рекурсивний пошук на боку АПІ
     * @param isRecursive чи потрібно використовувати рекурсивний пошук
     * @return {@link ListQuery<T>}
     */
    public ListQuery<T> recursive(boolean isRecursive) {
        this.isRecursive = isRecursive;
        return this;
    }

    /**
     * Визначає чи потрібно використовувати пагінацію при отриманні даних з API.
     * За замовчуванням пагінація увімкнена, тобто, дані будуть отримуватись по частинах
     * (з обмеженням на кількість отриманих даних у відповіді, вказати кількість отриманих даних можна методом limitAPI(int limit)).
     * Якщо пагінацію вимкнути, то будуть отримані всі доступні елементи за один запит до API, що може призвести до не очікуваних результатів,
     * або не ефективної праці з великими обсягами даних
     * @param isPagination true для увімкнення пагінації (за замовчуванням),
     *                     false для вимкнення пагінації.
     * @return {@link ListQuery<T>}
     */
    public ListQuery<T> pagination(boolean isPagination) {
        this.pagination = isPagination;
        return this;
    }

    /**
     * Фільтр який буде фільтрувати отримані елементи після отримання з АПІ
     * @param filter функція фільтра
     * @return {@link ListQuery<T>}
     */
    public ListQuery<T> filter(Predicate<T> filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Кількість елементів з АПІ ви бажаєте отримати.
     * Після наповнення листа потрібною кількістю елементів цикл запитів до АПІ буде перерваний, якщо увімкнена пагінація
     * Якщо ви шукаєте одну директорію, просто вкажіть 1, щоб одразу завершити пошук, та перестати викликати АПІ.
     * @param maxResults кількість елементів ви хочете отримати
     * @return {@link ListQuery<T>}
     */
    public ListQuery<T> maxResults(Integer maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    protected SourceFilesApi getSourceFilesApi() {
        return sourceFilesApi;
    }

    protected long getProjectID() {
        return projectID;
    }


    protected List<T> listWithPagination(long projectID) {
        int offset = this.offsetApi;
        List<T> resultElements = new ArrayList<>();
        List<T> responseElements;
        do {
            responseElements = this.fetchFromAPI(projectID, null,
                    this.directoryID, this.filterApi, this.isRecursive, this.limitApi, offset);
            // Додаємо дані про файли до загального списку про файли
            if (this.filter != null) { // Якщо є фільтр додаємо відфільтровані дані
                if (this.maxResults != null) {
                    int remainingSpace = this.maxResults - resultElements.size();
                    resultElements.addAll(responseElements.stream().filter(this.filter).limit(remainingSpace).toList());
                } else {
                    resultElements.addAll(responseElements.stream().filter(this.filter).toList());
                }
            } else {
                if (this.maxResults != null) {
                    int remainingSpace = this.maxResults - resultElements.size();
                    resultElements.addAll(responseElements.stream().limit(remainingSpace).toList());
                } else {
                    resultElements.addAll(responseElements);
                }
            }
            // Оновлюємо offset враховуючи кількість повернутих директорій
            offset += responseElements.size();
        } while (this.shouldContinuePagination(responseElements, resultElements)); // Перевіряємо чи потрібно продовжувати пагінацію

        return resultElements;
    }
    protected abstract List<T> fetchFromAPI(long projectID, Long branchID, Long directoryID, String filter, boolean isRecursion, int limit, int offset);
    private boolean shouldContinuePagination(List<T> responseElements, List<T> resultElement) {
        boolean hasMore = responseElements.size() == this.limitApi;// Чи є ще елементи які можна отримати з API
        boolean isNotMaxResults = this.maxResults == null || resultElement.size() >= this.maxResults; // Якщо вказано максимальну кількість елементів, то перевіряємо чи досягли цього
        return this.isRecursive && hasMore && isNotMaxResults;
    }
}
