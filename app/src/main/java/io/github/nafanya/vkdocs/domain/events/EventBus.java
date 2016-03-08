package io.github.nafanya.vkdocs.domain.events;

import rx.Observable;

public interface EventBus {
    Observable<?> putEvent(Class<?> clazz, Observable<?> observable);
    Observable<?> getEvent(Class<?> clazz);
    void removeEvent(Class<?> clazz);
}
