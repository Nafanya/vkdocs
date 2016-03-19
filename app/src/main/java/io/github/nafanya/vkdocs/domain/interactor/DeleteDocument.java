package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;

public class DeleteDocument extends UseCase<Integer> {

    private DocumentRepository repository;
    private VkDocument doc;

    public DeleteDocument(Scheduler observerScheduler,
                          Scheduler subscriberScheduler,
                          EventBus eventBus,
                          DocumentRepository repository,
                          VkDocument doc) {
        super(observerScheduler, subscriberScheduler, eventBus, false);
        this.repository = repository;
        this.doc = doc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<Integer> buildUseCase() {
        return Observable.create(subscriber ->  {
            try {
                eventBus.removeEvent(GetMyDocuments.class);
                repository.delete(doc);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
