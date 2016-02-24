package io.github.nafanya.vkdocs.domain.interactor.base;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.observers.Subscribers;
import rx.subscriptions.Subscriptions;

public abstract class UseCase<T> {
    private Subscription subscription = Subscriptions.empty();
    private final Scheduler subscriberScheduler;
    private final Scheduler observerScheduler;
    private boolean isCached;
    private EventBus eventBus;

    public UseCase(Scheduler observerScheduler, Scheduler subscriberScheduler,
                   EventBus eventBus, boolean isCached) {
        this.subscriberScheduler = subscriberScheduler;
        this.observerScheduler = observerScheduler;
        this.isCached = isCached;
        this.eventBus = eventBus;
    }

    public UseCase(Scheduler observerScheduler, Scheduler subscriberScheduler, EventBus eventBus) {
        this.subscriberScheduler = subscriberScheduler;
        this.observerScheduler = observerScheduler;
        this.isCached = true;
        this.eventBus = eventBus;
    }

    public abstract Observable<T> buildUseCase();

    @SuppressWarnings("unchecked")
    public void execute() {
        subscription = getObservable().subscribe(Subscribers.empty());
    }


    public void execute(Subscriber<T> subscriber) {
        subscription = getObservable().subscribe(subscriber);
    }

    @SuppressWarnings("unchecked")
    private Observable<T> getObservable() {
        Observable<T> observable;
        if (isCached) {
            observable = (Observable<T>)eventBus.getEvent(getClass());
            if (observable == null)
                observable = (Observable<T>)eventBus.putEvent(getClass(), buildUseCase());
        } else
            observable = buildUseCase();

        return observable.observeOn(observerScheduler).
                subscribeOn(subscriberScheduler);
    }

    public void unsubscribe() {
        if (!subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
