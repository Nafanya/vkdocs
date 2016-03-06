package io.github.nafanya.vkdocs.domain.interactor;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

public class DeleteDocument extends UseCase<Integer> {

    private DocumentRepository repository;
    private VKApiDocument doc;

    public DeleteDocument(Scheduler observerScheduler,
                          Scheduler subscriberScheduler,
                          EventBus eventBus, boolean isCached,
                          DocumentRepository repository,
                          VKApiDocument doc) {
        super(observerScheduler, subscriberScheduler, eventBus, isCached);
        this.repository = repository;
        this.doc = doc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<Integer> buildUseCase() {
        return Observable.create(subscriber ->  {
            try {
                repository.delete(doc);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
