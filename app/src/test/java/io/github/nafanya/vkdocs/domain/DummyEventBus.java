package io.github.nafanya.vkdocs.domain;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import rx.Observable;

/**
 * Created by pva701 on 24.02.16.
 */
public class DummyEventBus implements EventBus {
    @Override
    public Observable<?> putEvent(Class<?> clazz, Observable<?> observable) {
        return observable;
    }

    @Override
    public Observable<?> getEvent(Class<?> clazz) {
        return null;
    }
}
