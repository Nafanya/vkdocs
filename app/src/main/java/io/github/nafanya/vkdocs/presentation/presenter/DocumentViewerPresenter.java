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
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DocumentViewerPresenter extends BasePresenter {

    public interface Callback {
        void onCompleteCaching(VkDocument document);
        void onProgress(int progress);
        void onError(Exception e);
    }

    protected final Scheduler OBSERVER = AndroidSchedulers.mainThread();
    protected final Scheduler SUBSCRIBER = Schedulers.io();

    protected CacheManager cacheManager;
    protected Callback callback;
    protected EventBus eventBus;
    protected DocumentRepository repository;
    protected Subscriber<VkDocument> cacheSubscriber = Subscribers.empty();
    protected VkDocument currentDocument;
    protected boolean isAlreadyOfflineInProgress;
    protected boolean isDownloading;

    protected DownloadRequest.RequestListener downloadingProgressListener = new DownloadRequest.RequestListener() {
        @Override
        public void onProgress(int percentage) {
            callback.onProgress(percentage);
        }

        @Override
        public void onComplete() {
            callback.onCompleteCaching(currentDocument);
            int hash = CacheDocument.hashByDoc(currentDocument);
            eventBus.removeEvent(hash);
            isDownloading = false;
        }

        @Override
        public void onError(Exception e) {
            callback.onError(e);
        }
    };

    public DocumentViewerPresenter(EventBus eventBus, DocumentRepository repository,
                                   CacheManager cacheManager, Callback callback) {
        this.cacheManager = cacheManager;
        this.eventBus = eventBus;
        this.repository = repository;
        this.callback = callback;

    }

    public void openDocument(VkDocument document) {
        currentDocument = document;
        isAlreadyOfflineInProgress = document.isOfflineInProgress();

        Timber.d("[open document] %s: request %s", currentDocument.title, currentDocument.getRequest());
        Timber.d("[open document] %s: cached = %b, offline = %b", currentDocument.title, currentDocument.isCached(), currentDocument.isOffline());
        cacheSubscriber = new CacheSubscriber();
        new CacheDocument(OBSERVER, SUBSCRIBER, eventBus, cacheManager, currentDocument).execute(cacheSubscriber);
        isDownloading = true;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void cancelDownloading() {
        if (!isAlreadyOfflineInProgress) {
            new CancelDownloadingDocument(OBSERVER, SUBSCRIBER, eventBus, repository, cacheManager.getDownloadManager(), currentDocument).execute();
            eventBus.removeEvent(CacheDocument.hashByDoc(currentDocument));
        }//TODO replace to interactor CancelCaching, because CacheManager managed caching documents
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
                isDownloading = false;
                callback.onCompleteCaching(document);
            } else
                document.getRequest().addListener(downloadingProgressListener);
        }
    }
}
