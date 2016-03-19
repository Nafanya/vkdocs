package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.net.base.OfflineManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.net.base.download.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Observable;
import rx.Scheduler;

public class MakeOfflineDocument extends UseCase<Void> {
    private VkDocument document;
    private String toPath;
    private OfflineManager offlineManager;

    public MakeOfflineDocument(Scheduler observerScheduler, Scheduler subscriberScheduler, EventBus eventBus,
                               OfflineManager offlineManager,
                               VkDocument document,
                               String toPath) {
        super(observerScheduler, subscriberScheduler, eventBus, false);
        this.offlineManager = offlineManager;
        this.document = document;
        this.toPath = toPath;
    }

    @Override
    public Observable<Void> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                eventBus.removeEvent(GetDocuments.class);
                offlineManager.makeOffline(document, toPath);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
