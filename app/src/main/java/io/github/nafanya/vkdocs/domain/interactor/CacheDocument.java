package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.net.base.download.DownloadManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

public class CacheDocument extends UseCase<VkDocument> {
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
    public Observable<VkDocument> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                DownloadRequest request = new DownloadRequest(document.url, toPath);
                request.setDocId(document.getId());
                request.setTotalBytes(document.size);
                request.addListener(new DownloadRequest.RequestListener() {
                    @Override
                    public void onProgress(int percentage) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("ON COMPL CACHE");
                        document.setPath(request.getDest());
                        document.resetRequest();
                        new UpdateDocument(subscriberScheduler, eventBus, repository, document).execute();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                downloadManager.enqueue(request);

                document.setOfflineType(VkDocument.CACHE);
                document.setRequest(request);
                repository.update(document);
                eventBus.removeEvent(GetDocuments.class);

                subscriber.onNext(document);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
