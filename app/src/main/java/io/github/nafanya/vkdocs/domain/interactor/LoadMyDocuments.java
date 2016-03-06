package io.github.nafanya.vkdocs.domain.interactor;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/*
Загружает доки с сервера и синхронизирует их с базой или бросает эксешпн, если получить доки не удалось.
 */
public class LoadMyDocuments extends UseCase<List<VKApiDocument>> {
    private DocumentRepository repository;
    public LoadMyDocuments(Scheduler observerScheduler, Scheduler subscriberScheduler,
                           EventBus eventBus, boolean isCached, DocumentRepository repository) {
        super(observerScheduler, subscriberScheduler, eventBus, isCached);
        this.repository = repository;
    }

    @Override
    public Observable<List<VKApiDocument>> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                repository.synchronize();
                subscriber.onNext(repository.getMyDocuments());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
