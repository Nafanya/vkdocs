package io.github.nafanya.vkdocs.domain.events;

import android.util.LruCache;

import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;

public class LruEventBus implements EventBus {
    private LruCache<Integer, UseCase<?>> cache;

    public LruEventBus(int size) {
        cache = new LruCache<>(size);
    }

    @Override
    public synchronized void putEvent(UseCase<?> useCase) {
        cache.put(useCase.hashCode(), useCase);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T> UseCase<T> getEvent(int hash) {
        return (UseCase<T>)cache.get(hash);
    }

    @Override
    public synchronized void removeEvent(int hash) {
        cache.remove(hash);
    }

    @Override
    public synchronized boolean contains(int hash) {
        return getEvent(hash) != null;
    }

    @Override
    public synchronized void removeEvent(Class<? extends UseCase<?>> clazz) {
        removeEvent(clazz.hashCode());
    }

    @Override
    public synchronized boolean contains(Class<? extends UseCase<?>> clazz) {
        return getEvent(clazz.hashCode()) != null;
    }

    @Override
    public synchronized <T> UseCase<T> getEvent(Class<? extends UseCase<T>> clazz) {
        return getEvent(clazz.hashCode());
    }
}