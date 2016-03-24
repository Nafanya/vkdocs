package io.github.nafanya.vkdocs.domain.interactor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

/*
Получает доки из локальной базы данных.
*/

public class GetDocuments extends UseCase<List<VkDocument>> {
    private DocumentRepository repository;
    private static final Object lock = new Object();
    private static ConcurrentHashMap<Integer, VkDocument> documents;

    public GetDocuments(Scheduler observerScheduler, Scheduler subscriberScheduler,
                        EventBus eventBus,
                        DocumentRepository repository) {
        super(observerScheduler, subscriberScheduler, eventBus, true);
        this.repository = repository;
    }

    public static void synchronizeWithHashMap(List<VkDocument> docs) {
        synchronized (lock) {
            documents = new ConcurrentHashMap<>();
            for (VkDocument d : docs)
                documents.put(d.getId(), d);
        }
    }

    @Override
    public Observable<List<VkDocument>> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                List<VkDocument> docs = repository.getMyDocuments();
                synchronizeWithHashMap(docs);
                subscriber.onNext(docs);
                try {
                    repository.synchronize();//Get data from network and synchronize it
                    docs = repository.getMyDocuments();
                    synchronizeWithHashMap(docs);
                    subscriber.onNext(docs);
                } catch (Exception ignore) {}
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public static void update(VkDocument document) {
        synchronized (lock) {
            VkDocument doc = documents.get(document.getId());
            if (doc == null)
                documents.put(document.getId(), document.copy());
            else
                doc.copyFrom(document);

        }
    }

    public static VkDocument getDocument(int docId) {
        return documents.get(docId).copy();
    }

    public static VkDocument getDocument(VkDocument document) {
        return documents.get(document.getId()).copy();
    }
}
