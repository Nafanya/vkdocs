package io.github.nafanya.vkdocs.domain.interactor.base;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public abstract class UseCase<T> {
    private Observable<T> observable;
    protected Subscription subscription = Subscriptions.empty();
    protected final Scheduler subscriberScheduler;
    protected final Scheduler observerScheduler;
    protected boolean isCached;
    protected EventBus eventBus;


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

    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return hashCode() == o.hashCode();
    }

    @SuppressWarnings("unchecked")
    public void execute() {
        subscription = getUseCase().observable.subscribe(Subscribers.empty());
    }


    public void execute(Subscriber<T> subscriber) {
        subscription = getUseCase().observable.subscribe(subscriber);
    }

    @SuppressWarnings("unchecked")
    private UseCase<T> getUseCase() {
        UseCase<T> useCase = eventBus.getEvent(this.hashCode());
        if (useCase == null) {
            if (isCached)
                eventBus.putEvent(this);
            useCase = this;
            observable = applySchedulers(buildUseCase().cache());
        }
        return useCase;
    }

    public void unsubscribe() {
        if (!subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    private Observable<T> applySchedulers(Observable<T> observable) {
        return observable.observeOn(observerScheduler).subscribeOn(subscriberScheduler);
    }
}
