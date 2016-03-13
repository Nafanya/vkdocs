package io.github.nafanya.vkdocs.domain.events;

import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;

public interface EventBus {
    void putEvent(UseCase<?> useCase);
    <T> UseCase<T> getEvent(int hash);
    void removeEvent(int hash);
    boolean contains(int hash);

    void removeEvent(Class<? extends UseCase<?>> clazz);
    boolean contains(Class<? extends UseCase<?>> clazz);
    <T> UseCase<T> getEvent(Class<? extends UseCase<T>> clazz);
}
