package io.github.nafanya.vkdocs.presentation.presenter.base;

import rx.Scheduler;

public abstract class AbstractPresenter extends BasePresenter {
    private Scheduler observerScheduler;//ui thread
    private Scheduler subscriberScheduler;//background thread

    public AbstractPresenter(Scheduler observerScheduler, Scheduler subscriberScheduler) {
        this.observerScheduler = observerScheduler;
        this.subscriberScheduler = subscriberScheduler;
    }
}
