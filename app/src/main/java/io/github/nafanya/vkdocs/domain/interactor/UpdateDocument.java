package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class UpdateDocument extends UseCase<Void> {
    private DocumentRepository repository;
    private VkDocument document;

    public UpdateDocument(Scheduler subscriberScheduler, EventBus eventBus,
                          DocumentRepository repository, VkDocument document) {
        super(AndroidSchedulers.mainThread(), subscriberScheduler, eventBus, false);
        this.repository = repository;
        this.document = document;
    }

    @Override
    public Observable<Void> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                GetDocuments.update(document);
                repository.update(document);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
