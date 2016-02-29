package io.github.nafanya.vkdocs.domain.interactor;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/*
Получает доки из локальной базы данных.
*/

public class GetMyDocuments extends UseCase<List<VKApiDocument>> {
    private DocumentRepository repository;

    public GetMyDocuments(Scheduler observerScheduler, Scheduler subscriberScheduler,
                          EventBus eventBus,
                          boolean isCached,
                          DocumentRepository repository) {
        super(observerScheduler, subscriberScheduler, eventBus, isCached);
        this.repository = repository;
    }

    @Override
    public Observable<List<VKApiDocument>> buildUseCase() {
        return Observable.create(new Observable.OnSubscribe<List<VKApiDocument>>() {
            @Override
            public void call(Subscriber<? super List<VKApiDocument>> subscriber) {
                try {
                    subscriber.onNext(repository.getMyDocuments());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
