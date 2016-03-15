package io.github.nafanya.vkdocs.presentation.presenter.base;

import android.os.Environment;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.domain.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.CacheDocument;
import io.github.nafanya.vkdocs.domain.interactor.CancelDownloadingDocument;
import io.github.nafanya.vkdocs.domain.interactor.GetMyDocuments;
import io.github.nafanya.vkdocs.domain.interactor.MakeOfflineDocument;
import io.github.nafanya.vkdocs.domain.interactor.NetworkMyDocuments;
import io.github.nafanya.vkdocs.domain.interactor.UpdateDocument;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.InternetService;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DocumentsPresenter extends BasePresenter {

    public interface Callback {
        void onGetDocuments(List<VkDocument> documents);
        void onNetworkError(Exception ex);
        void onDatabaseError(Exception ex);
        void onMakeOffline(Exception ex);
        void onRename(Exception ex);
        void onDelete(Exception ex);

        void onOpenDocument(VkDocument document);
        void onAlreadyDownloading(VkDocument document, boolean isReallyAlreadyDownloading);
    }

    private String OFFLINE_PATH = Environment.getExternalStorageDirectory().getPath() + "/VKDocs/offline/";
    private String CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/VKDocs/cache/";

    protected Subscriber<List<VkDocument>> documentsSubscriber = Subscribers.empty();
    protected Subscriber<List<VkDocument>> networkSubscriber = Subscribers.empty();
    protected Subscriber<VkDocument> cacheSubscriber = Subscribers.empty();

    protected DocFilter filter;
    protected InterruptableDownloadManager downloadManager;
    protected Callback callback;
    protected EventBus eventBus;
    protected DocumentRepository repository;
    protected InternetService internetService;

    public DocumentsPresenter(DocFilter filter, EventBus eventBus,
                              DocumentRepository repository,
                              InterruptableDownloadManager downloadManager,
                              InternetService internetService,
                              Callback callback) {
        this.filter = filter;
        this.downloadManager = downloadManager;
        this.callback = callback;
        this.eventBus = eventBus;
        this.repository = repository;
        this.internetService = internetService;
    }

    public void setFilter(DocFilter filter) {
        this.filter = filter;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void updateDocument(VkDocument document) {
        new UpdateDocument(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus,
                repository, document).execute();
    }

    //TODO when caching is finished, remove GetDocuments from EventBus?
    public void openDocument(VkDocument document) {
        Timber.d("[open document] %s: request %s", document.title, document.getRequest());
        if (document.isOffline() || document.isCached())
            callback.onOpenDocument(document);
        else {
            if (!document.isDownloading()) {
                cacheSubscriber = new CacheSubscriber();
                new CacheDocument(AndroidSchedulers.mainThread(), Schedulers.io(),
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

    //TODO add on progress callback for more informative?
    public void makeOffline(VkDocument document) {
        new MakeOfflineDocument(
                AndroidSchedulers.mainThread(),
                Schedulers.io(),
                eventBus,
                document,
                OFFLINE_PATH + document.title,
                repository,
                downloadManager).execute();
    }

    public void cancelDownloading(VkDocument document) {
        new CancelDownloadingDocument(
                AndroidSchedulers.mainThread(),
                Schedulers.io(),
                eventBus,
                repository,
                downloadManager,
                document).execute();
    }

    public void retryDownloadDocument(VkDocument document) {
        List<DownloadRequest> requests = downloadManager.getQueue();
        for (DownloadRequest req: requests)
            if (req.getDocId() == document.getId()) {
                document.setRequest(req);
                req.resetError();//smth holy shit
                break;
            }
        openDocument(document);
    }

    public void rename(VKApiDocument document, String newName) {

    }

    public void delete(VKApiDocument document) {

    }

    public void forceNetworkLoad() {
        networkSubscriber = new NetworkSubscriber();
        new NetworkMyDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, repository).
                execute(networkSubscriber);
    }

    public void getDocuments() {
        Timber.d("get documents");
        documentsSubscriber = new GetDocumentsSubscriber();
        new GetMyDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, repository).execute(documentsSubscriber);
    }

    @Override
    public void onStart() {
        if (eventBus.contains(NetworkMyDocuments.class) && networkSubscriber.isUnsubscribed()) {
            networkSubscriber = new NetworkSubscriber();
            eventBus.getEvent(NetworkMyDocuments.class).execute(networkSubscriber);
        }

        if (eventBus.contains(CacheDocument.class) && cacheSubscriber.isUnsubscribed()) {
            cacheSubscriber = new CacheSubscriber();
            eventBus.getEvent(CacheDocument.class).execute(cacheSubscriber);
        }
    }

/*    @Override
    public void onResume() {
        if (eventBus.contains(GetMyDocuments.class) && documentsSubscriber.isUnsubscribed()) {
            documentsSubscriber = new GetDocumentsSubscriber();
            eventBus.getEvent(GetMyDocuments.class).execute(documentsSubscriber);
        }
    }*/

    @Override
    public void onStop() {
        unsubscribeIfNot(documentsSubscriber);
        unsubscribeIfNot(networkSubscriber);
        unsubscribeIfNot(cacheSubscriber);
    }

    private void unsubscribeIfNot(Subscriber<?> subscriber) {
        if (!subscriber.isUnsubscribed())
            subscriber.unsubscribe();
    }

    public class GetDocumentsSubscriber extends DefaultSubscriber<List<VkDocument>> {
        @Override
        public void onNext(List<VkDocument> vkDocuments) {
            if (callback != null) {//get actual download requests with correct request observer
                List<DownloadRequest> requests = downloadManager.getQueue();
                List<VkDocument> documents = filterList(vkDocuments);
                for (VkDocument d: documents)
                    for (DownloadRequest req: requests)
                        if (req.getDocId() == d.getId()) {
                            d.setRequest(req);
                            break;
                        }
                callback.onGetDocuments(copyVkDocumentsList(filterList(documents)));
            }
        }

        private List<VkDocument> copyVkDocumentsList(List<VkDocument> docs) {
            List<VkDocument> ret = new ArrayList<>();
            for (VkDocument x : docs)
                ret.add(x.copy());
            return ret;
        }

        @Override
        public void onError(Throwable e) {
            if (callback != null)
                callback.onDatabaseError((Exception)e);
        }
    }

    public class NetworkSubscriber extends DefaultSubscriber<List<VkDocument>> {
        @Override
        public void onNext(List<VkDocument> vkApiDocuments) {
            if (callback != null)
                callback.onGetDocuments(filterList(vkApiDocuments));
        }

        @Override
        public void onError(Throwable e) {
            if (callback != null)
                callback.onNetworkError((Exception) e);
        }
    }

    public class CacheSubscriber extends DefaultSubscriber<VkDocument> {

        @Override
        public void onNext(VkDocument document) {
            callback.onAlreadyDownloading(document, false);
        }
    }

    protected List<VkDocument> filterList(List<VkDocument> list) {
        List<VkDocument> ret = new ArrayList<>();
        for (VkDocument x : list)
            if (filter.filter(x))
                ret.add(x);
        return ret;
    }
}