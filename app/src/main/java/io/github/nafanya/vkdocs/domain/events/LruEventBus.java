package io.github.nafanya.vkdocs.domain.events;

import android.util.LruCache;

import rx.Observable;

public class LruEventBus implements EventBus {
    private LruCache<Class<?>, Observable<?>> cache;

    public LruEventBus(int size) {
        cache = new LruCache<>(size);
    }

    @Override
    public Observable<?> putEvent(Class<?> clazz, Observable<?> observable) {
        observable = observable.cache();
        cache.put(clazz, observable);
        return observable;
    }

    @Override
    public Observable<?> getEvent(Class<?> clazz) {
        return cache.get(clazz);
    }
}
