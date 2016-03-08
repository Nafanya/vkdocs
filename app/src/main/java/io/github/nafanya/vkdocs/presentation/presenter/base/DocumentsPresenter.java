package io.github.nafanya.vkdocs.presentation.presenter.base;

import android.os.Environment;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.CancelMakeOffline;
import io.github.nafanya.vkdocs.domain.interactor.GetMyDocuments;
import io.github.nafanya.vkdocs.domain.interactor.LoadMyDocuments;
import io.github.nafanya.vkdocs.domain.interactor.MakeOfflineDocument;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
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
    }

    protected GetMyDocuments getDocumentsInteractor;
    protected LoadMyDocuments networkInteractor;
    protected Subscriber<List<VkDocument>> getDocumentsSubscriber = Subscribers.empty();
    protected Subscriber<List<VkDocument>> networkSubscriber = Subscribers.empty();
    protected DocFilter filter;
    protected DownloadManager<DownloadRequest> downloadManager;
    protected Callback callback;
    protected EventBus eventBus;
    protected DocumentRepository repository;

    public DocumentsPresenter(DocFilter filter, EventBus eventBus, DocumentRepository repository, DownloadManager<DownloadRequest> downloadManager, Callback callback) {
        this.filter = filter;
        this.getDocumentsInteractor = new GetMyDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, repository);
        this.networkInteractor = new LoadMyDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, repository);
        this.downloadManager = downloadManager;
        this.callback = callback;
        this.eventBus = eventBus;
        this.repository = repository;

    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    //TODO add on progress callback for more informative?
    public void makeOffline(VkDocument document) {
        new MakeOfflineDocument(
                AndroidSchedulers.mainThread(),
                Schedulers.io(),
                eventBus,
                document,
                Environment.getExternalStorageDirectory().getPath() + "/VKDocs/offline/" + document.title,
                repository,
                downloadManager).execute();
    }

    public void cancelMakeOffline(VkDocument document) {
        new CancelMakeOffline(
                AndroidSchedulers.mainThread(),
                Schedulers.io(),
                eventBus,
                repository,
                downloadManager,
                document).execute();
    }

    public void rename(VKApiDocument document, String newName) {

    }

    public void delete(VKApiDocument document) {

    }

    public void forceNetworkLoad() {
        networkSubscriber = new NetworkSubscriber();
        networkInteractor.execute(networkSubscriber);
    }

    public void getDocuments() {
        getDocumentsSubscriber = new DatabaseSubscriber();
        getDocumentsInteractor.execute(getDocumentsSubscriber);
    }

    @Override
    public void onStop() {
        if (!getDocumentsSubscriber.isUnsubscribed())
            getDocumentsSubscriber.unsubscribe();
        if (!networkSubscriber.isUnsubscribed())
            networkSubscriber.unsubscribe();
    }

    public class DatabaseSubscriber extends DefaultSubscriber<List<VkDocument>> {
        @Override
        public void onNext(List<VkDocument> vkDocuments) {
            if (callback != null) {//get actual download requests with correct request observer
                List<DownloadRequest> requests = downloadManager.getQueue();//TODO fix it or no, sync query to db from main
                List<VkDocument> documents = filterList(vkDocuments);
                for (VkDocument d: documents)
                    for (DownloadRequest req: requests)
                        if (req.getDocId() == d.getId()) {
                            d.setRequest(req);
                            break;
                        }
                callback.onGetDocuments(documents);
            }
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

    protected List<VkDocument> filterList(List<VkDocument> list) {
        List<VkDocument> ret = new ArrayList<>();
        for (VkDocument x : list)
            if (filter.filter(x))
                ret.add(x);
        return ret;
    }

    public interface DocFilter {
        boolean filter(VkDocument doc);
    }

    public static class SimpleDocFilter implements DocFilter {
        private String[] exts;
        public SimpleDocFilter(String... exts) {
            this.exts = exts;
        }

        @Override
        public boolean filter(VkDocument doc) {
            for (String ext : exts)
                if (ext.equals("*") ||
                        doc.ext != null && doc.ext.equals(ext) ||
                        doc.title == null || doc.title.endsWith(ext))
                    return true;
            return false;
        }
    }
}