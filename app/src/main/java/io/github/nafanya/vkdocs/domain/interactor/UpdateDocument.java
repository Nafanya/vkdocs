package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;

public class UpdateDocument extends UseCase<Void> {
    private DocumentRepository repository;
    private VkDocument document;

    public UpdateDocument(Scheduler observerScheduler, Scheduler subscriberScheduler, EventBus eventBus,
                          DocumentRepository repository, VkDocument document) {
        super(observerScheduler, subscriberScheduler, eventBus, false);
        this.repository = repository;
        this.document = document;
    }

    @Override
    public Observable<Void> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                repository.update(document);
                eventBus.removeEvent(GetMyDocuments.class);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
