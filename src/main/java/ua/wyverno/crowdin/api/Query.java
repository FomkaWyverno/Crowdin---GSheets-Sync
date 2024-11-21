package ua.wyverno.crowdin.api;

/**
 * Інтерфейс для створення та виконання Crowdin API запиту
 * @param <T> повернуті дані з Crowdin API
 */
public interface Query<T> {
    /**
     * Виконує запит до Crowdin API
     * @return повертає дані виконаного Endpoint
     */
    T execute();
}
