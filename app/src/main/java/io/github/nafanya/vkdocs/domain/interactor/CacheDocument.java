package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

public class CacheDocument extends UseCase<DownloadRequest> {
    private VkDocument document;
    private String toPath;
    private DocumentRepository repository;
    private DownloadManager downloadManager;

    public CacheDocument(Scheduler observerScheduler, Scheduler subscriberScheduler, EventBus eventBus,
                               VkDocument document,
                               String toPath,
                               DocumentRepository repository,
                               DownloadManager downloadManager) {
        super(observerScheduler, subscriberScheduler, eventBus, true);
        this.document = document;
        this.toPath = toPath;
        this.repository = repository;
        this.downloadManager = downloadManager;
    }

    @Override
    public Observable<DownloadRequest> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                Timber.d("IN CACHE DOC");
                DownloadRequest request = new DownloadRequest(document.url, toPath);
                request.setDocId(document.getId());
                request.setTotalBytes(document.size);
                downloadManager.enqueue(request);

                document.setOfflineType(VkDocument.CACHE);
                repository.update(document);
                subscriber.onNext(request);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
