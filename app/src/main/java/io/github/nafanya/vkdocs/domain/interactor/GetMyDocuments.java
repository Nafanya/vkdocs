package io.github.nafanya.vkdocs.domain.interactor;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;

/*
Получает доки из локальной базы данных.
*/

public class GetMyDocuments extends UseCase<List<VkDocument>> {
    private DocumentRepository repository;

    public GetMyDocuments(Scheduler observerScheduler, Scheduler subscriberScheduler,
                          EventBus eventBus,
                          boolean isCached,
                          DocumentRepository repository) {
        super(observerScheduler, subscriberScheduler, eventBus, isCached);
        this.repository = repository;
    }

    @Override
    public Observable<List<VkDocument>> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(repository.getMyDocuments());
                try {
                    repository.synchronize();//Get data from network and synchronize it
                    subscriber.onNext(repository.getMyDocuments());
                } catch (Exception ignore) {}

                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
