package io.github.nafanya.vkdocs.presentation.presenter.base;

import rx.Subscriber;

public abstract class BasePresenter {
    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onResume() method.
     */
    public void onResume() {}

    /**
     * Method that controls the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onPause() method.
     */
    public void onPause() {}

    /**
     * Method that controls the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onStopPlaying() method.
     */
    public void onStop() {}

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onDestroy() method.
     */
    public void onDestroy() {}

    public void onStart() {}

    protected void unsubscribeIfNot(Subscriber<?> subscriber) {
        if (!subscriber.isUnsubscribed())
            subscriber.unsubscribe();
    }
}