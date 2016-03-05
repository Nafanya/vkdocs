package io.github.nafanya.vkdocs.domain.interactor;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

public class MakeOfflineDocument extends UseCase<DownloadRequest> {
    private VKApiDocument document;
    private String toPath;
    private DocumentRepository repository;
    private DownloadManager<DownloadRequest> downloadManager;

    public MakeOfflineDocument(Scheduler observerScheduler, Scheduler subscriberScheduler, EventBus eventBus, boolean isCached,
                               VKApiDocument document,
                               String toPath,
                               DocumentRepository repository,
                               DownloadManager<DownloadRequest> downloadManager) {
        super(observerScheduler, subscriberScheduler, eventBus, isCached);
        this.document = document;
        this.toPath = toPath;
        this.repository = repository;
        this.downloadManager = downloadManager;
    }

    @Override
    public Observable<DownloadRequest> buildUseCase() {
        return Observable.create(new Observable.OnSubscribe<DownloadRequest>() {
            @Override
            public void call(Subscriber<? super DownloadRequest> subscriber) {
                try {
                    DownloadRequest request = new DownloadRequest(document.url, toPath);
                    downloadManager.enqueue(request);
                    //TODO if was offline flag, set it
                    subscriber.onNext(request);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
