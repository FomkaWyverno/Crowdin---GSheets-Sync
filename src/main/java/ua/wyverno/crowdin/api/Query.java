package ua.wyverno.crowdin.api;

public interface Query<T> {
    T execute();
}
