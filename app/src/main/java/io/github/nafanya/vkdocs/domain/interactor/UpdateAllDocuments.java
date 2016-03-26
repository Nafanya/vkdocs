package io.github.nafanya.vkdocs.domain.interactor;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class UpdateAllDocuments extends UseCase<Void> {
    private List<VkDocument> docs;
    private DocumentRepository repository;

    public UpdateAllDocuments(Scheduler subscriberScheduler, EventBus eventBus,
                              DocumentRepository repository, List<VkDocument> docs) {
        super(AndroidSchedulers.mainThread(), subscriberScheduler, eventBus, false);
        this.repository = repository;
        this.docs = docs;
    }

    @Override
    public Observable<Void> buildUseCase() {
        for (VkDocument d : docs)
            GetDocuments.update(d);
        return Observable.create(subscriber -> {
            try {
                repository.updateAll(docs);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
