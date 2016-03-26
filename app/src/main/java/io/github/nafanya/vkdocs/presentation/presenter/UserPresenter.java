package io.github.nafanya.vkdocs.presentation.presenter;

import android.support.annotation.NonNull;

import com.vk.sdk.api.model.VKApiUser;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.GetUserInfo;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.repository.UserRepository;
import io.github.nafanya.vkdocs.presentation.presenter.base.BasePresenter;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;

/**
 * Created by nafanya on 3/26/16.
 */
public class UserPresenter extends BasePresenter {
    public interface Callback {
        void onUserInfoLoaded(VKApiUser userInfo);
    }

    protected final Scheduler OBSERVER = AndroidSchedulers.mainThread();
    protected final Scheduler SUBSCRIBER = Schedulers.io();

    protected Subscriber<VKApiUser> userSubscriber = Subscribers.empty();

    protected EventBus eventBus;
    protected Callback callback;
    protected UserRepository repository;

    public UserPresenter(EventBus eventBus, UserRepository repository, @NonNull Callback callback) {
        this.eventBus = eventBus;
        this.repository = repository;
        this.callback = callback;
    }


    public void getUserInfo() {
        userSubscriber = new GetUserInfoSubscriber();
        new GetUserInfo(
                OBSERVER,
                SUBSCRIBER,
                eventBus,
                repository
        ).execute(userSubscriber);
    }

    @Override
    public void onStart() {
        if (eventBus.contains(GetUserInfo.class) && userSubscriber.isUnsubscribed()) {
            userSubscriber = new GetUserInfoSubscriber();
            eventBus.getEvent(GetUserInfo.class).execute(userSubscriber);
        }
    }

    @Override
    public void onStop() {
        unsubscribeIfNot(userSubscriber);
    }

    public class GetUserInfoSubscriber extends DefaultSubscriber<VKApiUser> {
        @Override
        public void onNext(VKApiUser user) {
            callback.onUserInfoLoaded(user);
        }
    }
}
