package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;

public class MakeOfflineDocument extends UseCase<Void> {
    private VkDocument document;
    private String toPath;
    private DocumentRepository repository;
    private DownloadManager downloadManager;

    public MakeOfflineDocument(Scheduler observerScheduler, Scheduler subscriberScheduler, EventBus eventBus,
                               VkDocument document,
                               String toPath,
                               DocumentRepository repository,
                               DownloadManager downloadManager) {
        super(observerScheduler, subscriberScheduler, eventBus, false);
        this.document = document;
        this.toPath = toPath;
        this.repository = repository;
        this.downloadManager = downloadManager;
    }

    @Override
    public Observable<Void> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                eventBus.removeEvent(GetMyDocuments.class);
                DownloadRequest request = new DownloadRequest(document.url, toPath);
                request.setDocId(document.getId());
                request.setTotalBytes(document.size);
                request.addListener(new DownloadRequest.RequestListener() {
                    @Override
                    public void onProgress(int percentage) {

                    }

                    @Override
                    public void onComplete() {
                        document.setPath(request.getDest());
                        new UpdateDocument(observerScheduler, subscriberScheduler, eventBus, repository, document).execute();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                downloadManager.enqueue(request);
                document.setOfflineType(VkDocument.OFFLINE);
                document.setRequest(request);
                repository.update(document);

                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
