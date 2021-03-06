package io.github.nafanya.vkdocs.presentation.presenter;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.CacheDocument;
import io.github.nafanya.vkdocs.domain.interactor.CancelDownloadingDocument;
import io.github.nafanya.vkdocs.domain.interactor.GetDocuments;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.base.CacheManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.presentation.presenter.base.BasePresenter;
import rx.Subscriber;
import rx.observers.Subscribers;
import timber.log.Timber;

public class DocumentViewerPresenter extends BasePresenter {

    public interface Callback {
        void onCompleteCaching(VkDocument document);
        void onProgress(int progress);
        void onError(Exception e);
    }

    protected CacheManager cacheManager;
    protected Callback callback;
    protected EventBus eventBus;
    protected DocumentRepository repository;
    protected Subscriber<VkDocument> cacheSubscriber = Subscribers.empty();
    protected VkDocument currentDocument;
    protected boolean isAlreadyOfflineInProgress;

    protected enum DownloadingState{IDLE, DOWNLOADING, DOWNLOADED}
    protected DownloadingState downState = DownloadingState.IDLE;

    protected DownloadRequest.RequestListener downloadingProgressListener = new DownloadRequest.RequestListener() {
        @Override
        public void onProgress(int percentage) {
            callback.onProgress(percentage);
        }

        @Override
        public void onComplete() {
            downState = DownloadingState.DOWNLOADED;
            callback.onCompleteCaching(currentDocument);
            int hash = CacheDocument.hashByDoc(currentDocument);
            eventBus.removeEvent(hash);
        }

        @Override
        public void onError(Exception e) {
            callback.onError(e);
        }
    };

    public DocumentViewerPresenter(EventBus eventBus,
                                   DocumentRepository repository,
                                   CacheManager cacheManager,
                                   VkDocument document,
                                   Callback callback) {
        this.cacheManager = cacheManager;
        this.eventBus = eventBus;
        this.repository = repository;
        this.callback = callback;
        currentDocument = GetDocuments.getDocument(document);
        document.copyFrom(currentDocument);
        if (currentDocument.isDownloaded())
            downState = DownloadingState.DOWNLOADED;
    }

    public void openDocument() {
        isAlreadyOfflineInProgress = currentDocument.isOfflineInProgress();

        Timber.d("[open document] %s: request %s", currentDocument.title, currentDocument.getRequest());
        Timber.d("[open document] %s: cached = %b, offline = %b", currentDocument.title, currentDocument.isCached(), currentDocument.isOffline());
        cacheSubscriber = new CacheSubscriber();
        new CacheDocument(OBSERVER, SUBSCRIBER, eventBus, cacheManager, currentDocument).execute(cacheSubscriber);

        if (!currentDocument.isDownloaded())
            downState = DownloadingState.DOWNLOADING;
    }

    public boolean isDownloading() {
        return downState == DownloadingState.DOWNLOADING;
    }

    public boolean isDownloaded() {
        return downState == DownloadingState.DOWNLOADED;
    }

    public void cancelDownloading() {
        if (!isAlreadyOfflineInProgress) {
            new CancelDownloadingDocument(OBSERVER, SUBSCRIBER, eventBus, repository, cacheManager.getDownloadManager(), currentDocument).execute();
            eventBus.removeEvent(CacheDocument.hashByDoc(currentDocument));
        }
        if (currentDocument.getRequest() != null)
            currentDocument.getRequest().removeListener(downloadingProgressListener);
    }

    public void retryOpen() {
        cacheManager.retryCache(currentDocument);
    }


    @Override
    public void onStart() {
        int hash = CacheDocument.hashByDoc(currentDocument);
        if (eventBus.contains(hash) && cacheSubscriber.isUnsubscribed()) {
            cacheSubscriber = new CacheSubscriber();
            eventBus.<VkDocument>getEvent(hash).execute(cacheSubscriber);
        }
    }

    @Override
    public void onStop() {
        if (currentDocument != null && currentDocument.isCacheInProgress())
            currentDocument.getRequest().removeListener(downloadingProgressListener);

        unsubscribeIfNot(cacheSubscriber);
    }

    public class CacheSubscriber extends DefaultSubscriber<VkDocument> {
        @Override
        public void onNext(VkDocument document) {
            currentDocument = document;
            if (document.isDownloaded()) {
                downState = DownloadingState.DOWNLOADED;
                callback.onCompleteCaching(document);
            } else
                document.getRequest().addListener(downloadingProgressListener);
        }
    }

    public void unsubscribe() {
        unsubscribeIfNot(cacheSubscriber);
    }
}
