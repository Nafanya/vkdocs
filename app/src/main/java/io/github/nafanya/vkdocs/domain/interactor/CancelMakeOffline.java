package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;

public class CancelMakeOffline extends UseCase<Void> {
    private VkDocument document;
    private DocumentRepository repository;
    private DownloadManager<DownloadRequest> downloadManager;

    public CancelMakeOffline(Scheduler observerScheduler, Scheduler subscriberScheduler,
                             EventBus eventBus,
                             DocumentRepository repository,
                             DownloadManager<DownloadRequest> downloadManager,
                             VkDocument document) {
        super(observerScheduler, subscriberScheduler, eventBus, false);
        this.downloadManager = downloadManager;
        this.document = document;
        this.repository = repository;
    }

    @Override
    public Observable<Void> buildUseCase() {
        return Observable.create(subscriber -> {
            document.setOfflineType(VkDocument.NONE);
            repository.update(document);
            downloadManager.cancelRequest(document.getRequest());
            subscriber.onCompleted();
        });
    }
}
