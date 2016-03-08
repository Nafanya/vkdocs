package io.github.nafanya.vkdocs.domain.interactor;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;

/*
Загружает доки с сервера и синхронизирует их с базой или бросает эксешпн, если получить доки не удалось.
 */
public class LoadMyDocuments extends UseCase<List<VkDocument>> {
    private DocumentRepository repository;
    public LoadMyDocuments(Scheduler observerScheduler, Scheduler subscriberScheduler,
                           EventBus eventBus, DocumentRepository repository) {
        super(observerScheduler, subscriberScheduler, eventBus, true);
        this.repository = repository;
    }

    @Override
    public Observable<List<VkDocument>> buildUseCase() {
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
