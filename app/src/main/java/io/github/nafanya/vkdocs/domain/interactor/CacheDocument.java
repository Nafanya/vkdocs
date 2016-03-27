package io.github.nafanya.vkdocs.domain.interactor;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.base.CacheManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.net.impl.download.InterruptableDownloadManager;
import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

public class CacheDocument extends UseCase<VkDocument> {
    private VkDocument document;
    private CacheManager cacheManager;
    private InterruptableDownloadManager downloadManager;

    public CacheDocument(Scheduler observerScheduler, Scheduler subscriberScheduler, EventBus eventBus,
                         CacheManager cacheManager, VkDocument document) {
        super(observerScheduler, subscriberScheduler, eventBus, true);
        this.document = document;
        this.cacheManager = cacheManager;
        this.downloadManager = (InterruptableDownloadManager) cacheManager.getDownloadManager();
    }

    private DownloadRequest findDownloadRequest(VkDocument doc) {
        DownloadRequest request = null;
        List<DownloadRequest> requests = downloadManager.getQueue();
        for (DownloadRequest r : requests)
            if (r.getDocId() == doc.getId()) {
                request = r;
                break;
            }
        return request;
    }

    @Override
    public Observable<VkDocument> buildUseCase() {
        return Observable.create(subscriber -> {
            Timber.d("[CacheDocument] cahcing " + document.title);
            if (document.getRequest() == null)
                document.setRequest(findDownloadRequest(document));

            if (document.isDownloaded()) {
                Timber.d("[CacheDocument] already %s", document.title);
                subscriber.onNext(document);
            } else if (document.isDownloading()) {
                if (document.getRequest().isActive()) {
                    Timber.d("DOWNLOADING IS ACTIVE %s", document.title);
                    subscriber.onNext(document);
                } else {
                    Timber.d("DOWNLOADING RETRY %s", document.title);
                    downloadManager.retry(document.getRequest());
                    subscriber.onNext(document);
                }
            } else {
                Timber.d("CACHE %s", document.title);
                cacheManager.cache(document);
                subscriber.onNext(document);
            }
            subscriber.onCompleted();
        });
    }

    private static int P = 17239;
    private static int MOD = 1_000_000_000 + 9;
    public static int hashByDoc(VkDocument document) {
        if (document == null)
            return -1;
        return (int)((1L * document.getId() * P + 19381292) % MOD);
    }

    @Override
    public int hashCode() {
        return hashByDoc(document);
    }
}
