package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.net.base.download.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;

public class CancelDownloadingDocument extends UseCase<Void> {
    private VkDocument document;
    private DocumentRepository repository;
    private DownloadManager downloadManager;

    public CancelDownloadingDocument(Scheduler observerScheduler, Scheduler subscriberScheduler,
                                     EventBus eventBus, DocumentRepository repository,
                                     DownloadManager downloadManager, VkDocument document) {
        super(observerScheduler, subscriberScheduler, eventBus, false);
        this.downloadManager = downloadManager;
        this.document = document;
        this.repository = repository;
    }

    @Override
    public Observable<Void> buildUseCase() {
        return Observable.create(subscriber -> {
            document.setOfflineType(VkDocument.NONE);
            downloadManager.cancelRequest(document.getRequest());
            document.setRequest(null);
            new UpdateDocument(observerScheduler, eventBus, repository, document).execute();
            subscriber.onCompleted();
        });
    }
}
