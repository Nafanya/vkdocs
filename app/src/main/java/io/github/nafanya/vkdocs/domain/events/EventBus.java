package io.github.nafanya.vkdocs.domain.events;

import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;

public interface EventBus {
    void putEvent(UseCase<?> useCase);
    <T> UseCase<T> getEvent(int hash);
    void removeEvent(int hash);
    default boolean contains(int hash) {
        return getEvent(hash) != null;
    }

    default void removeEvent(Class<? extends UseCase<?>> clazz) {
        removeEvent(clazz.hashCode());
    }

    default boolean contains(Class<? extends UseCase<?>> clazz) {
        return getEvent(clazz.hashCode()) != null;
    }

    default <T> UseCase<T> getEvent(Class<? extends UseCase<T>> clazz) {
        return getEvent(clazz.hashCode());
    }
}
