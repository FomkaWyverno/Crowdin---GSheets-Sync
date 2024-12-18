package ua.wyverno.crowdin.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Базовий клас для запиту, який збирає листи з елементами
 * @param <T> Генерик тип елементів листа які має повернути запит до АПІ
 * @param <Q> Основний клас запиту який має виконувати АПІ запит, і обов'язково має успадковувати клас {@link ListQuery},
 *           має бути обов'язково вказаний, щоб методи які повертають себе, за патерном Builder, повертали успадкований клас запиту, а не цей абстракний клас.
 */
public abstract class ListQuery<T, Q extends ListQuery<T, Q>> implements Query<List<T>> {
    private int offsetApi = 0;
    private int limitApi = 100;
    private boolean pagination = true;
    private Predicate<T> filter;
    private Integer maxResults = null;



    /**
     * Вказує з якого елемента потрібно збирати елементи
     * @param offset індифікатор з якого елемента потрібно почати пошук
     * @return {@link Q}
     */
    public Q offset(int offset) {
        this.offsetApi = offset;
        return this.self();
    }

    /**
     * Обмежити кількість отриманих з АПІ елементів від 0 до 100
     * @param limit кількість елементів
     * @return {@link Q}
     */
    public Q limitAPI(int limit) {
        this.limitApi = limit;
        return this.self();
    }

    /**
     * Визначає чи потрібно використовувати пагінацію при отриманні даних з API.
     * За замовчуванням пагінація увімкнена, тобто, дані будуть отримуватись по частинах
     * (з обмеженням на кількість отриманих даних у відповіді, вказати кількість отриманих даних можна методом limitAPI(int limit)).
     * Якщо пагінацію вимкнути, то будуть отримані всі доступні елементи за один запит до API, що може призвести до не очікуваних результатів,
     * або не ефективної праці з великими обсягами даних
     * @param isPagination true для увімкнення пагінації (за замовчуванням),
     *                     false для вимкнення пагінації.
     * @return {@link Q}
     */
    public Q pagination(boolean isPagination) {
        this.pagination = isPagination;
        return this.self();
    }

    /**
     * Фільтр який буде фільтрувати отримані елементи після отримання з АПІ
     * @param filter функція фільтра
     * @return {@link Q}
     */
    public Q filter(Predicate<T> filter) {
        this.filter = filter;
        return this.self();
    }

    /**
     * Кількість елементів з АПІ ви бажаєте отримати.
     * Після наповнення листа потрібною кількістю елементів цикл запитів до АПІ буде перерваний, якщо увімкнена пагінація
     * Якщо ви шукаєте одну директорію, просто вкажіть 1, щоб одразу завершити пошук, та перестати викликати АПІ.
     * @param maxResults кількість елементів ви хочете отримати
     * @return {@link ListQuery<T>}
     */
    public Q maxResults(Integer maxResults) {
        this.maxResults = maxResults;
        return this.self();
    }
    protected int getOffsetApi() {
        return this.offsetApi;
    }

    protected int getLimitApi() {
        return this.limitApi;
    }

    protected boolean isPagination() {
        return this.pagination;
    }

    protected Predicate<T> getFilter() {
        return this.filter;
    }

    protected Integer getMaxResults() {
        return this.maxResults;
    }

    protected List<T> listWithPagination() {
        int offset = this.offsetApi;
        List<T> resultElements = new ArrayList<>();
        List<T> responseElements;
        do {
            responseElements = this.fetchFromAPI(this.limitApi, offset);
            // Додаємо дані про елементи до загального списку елементів
            if (this.filter != null) { // Якщо є фільтр додаємо відфільтровані елементи
                if (this.maxResults != null) { // Якщо є максимальна кількість
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
            // Оновлюємо offset враховуючи кількість повернутих елементів
            offset += responseElements.size();
        } while (this.pagination && this.shouldContinue(responseElements, resultElements)); // Перевіряємо чи потрібно продовжувати пагінацію

        return resultElements;
    }
    protected abstract List<T> fetchFromAPI(int limitAPI, int offset);
    protected boolean shouldContinue(List<T> responseElements, List<T> resultElement) {
        boolean hasMore = responseElements.size() == this.getLimitApi();// Чи є ще елементи які можна отримати з API
        boolean isMaxResults = this.getMaxResults() != null && resultElement.size() >= this.getMaxResults();
        // Якщо вказано максимальну кількість елементів,
        // то перевіряємо чи досягли цього
        return hasMore && !isMaxResults;
    }

    @SuppressWarnings("uncheked")
    protected Q self() {
        return (Q) this;
    }
}
