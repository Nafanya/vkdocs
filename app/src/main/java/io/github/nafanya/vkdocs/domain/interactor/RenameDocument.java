package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class RenameDocument extends UseCase<Void> {
    private DocumentRepository repository;
    private VkDocument document;
    private String newName;

    public RenameDocument(Scheduler subscriberScheduler, EventBus eventBus,
                          DocumentRepository repository,
                          VkDocument document,
                          String newName) {
        super(AndroidSchedulers.mainThread(),subscriberScheduler, eventBus, false);
        this.repository = repository;
        this.document = document;
        this.newName = newName;
    }

    @Override
    public Observable<Void> buildUseCase() {
        document.title = newName;
        GetDocuments.update(document);
        return Observable.create(subscriber -> {
            try {
                repository.rename(document, newName);
                repository.synchronize();
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
