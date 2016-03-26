package io.github.nafanya.vkdocs.domain;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;

/**
 * Created by pva701 on 24.02.16.
 */
public class DummyEventBus implements EventBus {
/*    @Override
    public Observable<?> putEvent(Class<?> clazz, Observable<?> observable) {
        return observable;
    }

    @Override
    public Observable<?> getEvent(Class<?> clazz) {
        return null;
    }

    @Override
    public void removeEvent(Class<?> clazz) {

    }*/

    @Override
    public void putEvent(UseCase<?> useCase) {

    }

    @Override
    public <T> UseCase<T> getEvent(int hash) {
        return null;
    }

    @Override
    public void removeEvent(int hash) {

    }

    @Override
    public boolean contains(int hash) {
        return false;
    }

    @Override
    public void removeEvent(Class<? extends UseCase<?>> clazz) {

    }

    @Override
    public boolean contains(Class<? extends UseCase<?>> clazz) {
        return false;
    }

    @Override
    public <T extends UseCase<?>> T getEvent(Class<T> clazz) {
        return null;
    }

}
