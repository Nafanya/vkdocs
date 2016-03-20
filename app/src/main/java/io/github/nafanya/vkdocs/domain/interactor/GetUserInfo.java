package io.github.nafanya.vkdocs.domain.interactor;

import com.vk.sdk.api.model.VKApiUser;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.domain.repository.UserRepository;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by nafanya on 3/20/16.
 */
public class GetUserInfo extends UseCase<VKApiUser> {
    private UserRepository repository;

    public GetUserInfo(Scheduler observerScheduler, Scheduler subscriberScheduler,
                       EventBus eventBus, UserRepository repository) {
        super(observerScheduler, subscriberScheduler, eventBus, true);
        this.repository = repository;
    }

    @Override
    public Observable<VKApiUser> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(repository.getUserInfo());
                try {
                    repository.synchronize();
                    subscriber.onNext(repository.getUserInfo());
                } catch (Exception ignore) {}
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
