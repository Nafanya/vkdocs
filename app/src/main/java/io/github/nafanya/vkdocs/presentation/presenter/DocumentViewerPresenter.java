package io.github.nafanya.vkdocs.presentation.presenter;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.CacheDocument;
import io.github.nafanya.vkdocs.domain.interactor.CancelDownloadingDocument;
import io.github.nafanya.vkdocs.domain.interactor.GetUserInfo;
import io.github.nafanya.vkdocs.domain.interactor.NetworkDocuments;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.net.impl.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.presentation.presenter.base.BasePresenter;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DocumentViewerPresenter extends BasePresenter {

    public interface Callback {
        void onOpenDocument(VkDocument document);
        void onAlreadyDownloading(VkDocument document, boolean isReallyAlreadyDownloading);
    }
    protected final Scheduler OBSERVER = AndroidSchedulers.mainThread();
    protected final Scheduler SUBSCRIBER = Schedulers.io();

    private String CACHE_PATH;
    protected InterruptableDownloadManager downloadManager;
    protected Callback callback;
    protected EventBus eventBus;
    protected DocumentRepository repository;
    protected Subscriber<VkDocument> cacheSubscriber = Subscribers.empty();

    public DocumentViewerPresenter(EventBus eventBus,
                              DocumentRepository repository,
                              InterruptableDownloadManager downloadManager,
                              File cacheRoot, Callback callback) {
        this.downloadManager = downloadManager;
        this.eventBus = eventBus;
        this.repository = repository;
        this.CACHE_PATH = cacheRoot.getAbsolutePath() + File.separator;
        this.callback = callback;
    }

    public void openDocument(VkDocument document) {
        Timber.d("[open document] %s: request %s", document.title, document.getRequest());
        Timber.d("[open document] %s: cached = %b, offline = %b", document.title, document.isCached(), document.isOffline());
        if (document.getRequest() == null)
            document.setRequest(findDownloadRequest(document));

        if (document.isOffline() || document.isCached())
            callback.onOpenDocument(document);
        else {
            if (!document.isDownloading()) {
                cacheSubscriber = new CacheSubscriber();
                new CacheDocument(OBSERVER, SUBSCRIBER,
                        eventBus,
                        document,
                        CACHE_PATH + document.title,
                        repository,
                        downloadManager).execute(cacheSubscriber);
            } else {
                if (document.getRequest().isActive())
                    callback.onAlreadyDownloading(document, true);
                else {
                    downloadManager.retry(document.getRequest());
                    callback.onAlreadyDownloading(document, true);
                }
            }
        }
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

    public void cancelDownloading(VkDocument document) {
        new CancelDownloadingDocument(
                OBSERVER,
                SUBSCRIBER,
                eventBus,
                repository,
                downloadManager,
                document).execute();
    }

    @Override
    public void onStart() {
        if (eventBus.contains(CacheDocument.class) && cacheSubscriber.isUnsubscribed()) {
            cacheSubscriber = new CacheSubscriber();
            eventBus.getEvent(CacheDocument.class).execute(cacheSubscriber);
        }
    }


    @Override
    public void onStop() {
        unsubscribeIfNot(cacheSubscriber);
    }

    public class CacheSubscriber extends DefaultSubscriber<VkDocument> {
        @Override
        public void onNext(VkDocument document) {
            if (callback != null) {
                callback.onAlreadyDownloading(document, false);
                eventBus.removeEvent(CacheDocument.class);
            }
        }
    }
}
