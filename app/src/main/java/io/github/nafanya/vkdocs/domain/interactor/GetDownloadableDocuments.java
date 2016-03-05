package io.github.nafanya.vkdocs.domain.interactor;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 * Created by pva701 on 05.03.16.
 */
public class GetDownloadableDocuments extends UseCase<List<VKApiDocument>> {
    private DocumentRepository repository;
    private DownloadManager<DownloadRequest> downloadManager;
    public GetDownloadableDocuments(Scheduler observerScheduler, Scheduler subscriberScheduler,
                          EventBus eventBus,
                          boolean isCached,
                          DocumentRepository repository,
                          DownloadManager<DownloadRequest> downloadManager) {
        super(observerScheduler, subscriberScheduler, eventBus, isCached);
        this.repository = repository;
        this.downloadManager = downloadManager;
    }

    @Override
    public Observable<List<VKApiDocument>> buildUseCase() {
        return Observable.create(new Observable.OnSubscribe<List<VKApiDocument>>() {
            @Override
            public void call(Subscriber<? super List<VKApiDocument>> subscriber) {
                try {
                    List<DownloadRequest> requests = downloadManager.getQueue();
                    List<VKApiDocument> documents = repository.getMyDocuments();
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
