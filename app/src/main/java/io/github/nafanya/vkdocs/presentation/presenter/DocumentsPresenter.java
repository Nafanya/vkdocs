package io.github.nafanya.vkdocs.presentation.presenter;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.vk.sdk.api.model.VKApiUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.CancelDownloadingDocument;
import io.github.nafanya.vkdocs.domain.interactor.DeleteDocument;
import io.github.nafanya.vkdocs.domain.interactor.GetDocuments;
import io.github.nafanya.vkdocs.domain.interactor.GetUserInfo;
import io.github.nafanya.vkdocs.domain.interactor.MakeOfflineDocument;
import io.github.nafanya.vkdocs.domain.interactor.NetworkDocuments;
import io.github.nafanya.vkdocs.domain.interactor.RenameDocument;
import io.github.nafanya.vkdocs.domain.interactor.UpdateDocument;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.domain.repository.UserRepository;
import io.github.nafanya.vkdocs.net.base.CacheManager;
import io.github.nafanya.vkdocs.net.base.OfflineManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.net.impl.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.presentation.presenter.base.BasePresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DocumentsPresenter extends BasePresenter {

    public interface Callback {
        void onGetDocuments(List<VkDocument> documents);
        void onNetworkDocuments(List<VkDocument> documents);
        void onNetworkError(Exception ex);
        void onDatabaseError(Exception ex);

        void onUpdatedDocument(VkDocument document);

        void onUserInfoLoaded(VKApiUser userInfo);
    }

    protected Subscriber<List<VkDocument>> documentsSubscriber = Subscribers.empty();
    protected Subscriber<List<VkDocument>> networkSubscriber = Subscribers.empty();
    protected Subscriber<VKApiUser> userSubscriber = Subscribers.empty();

    protected DocFilter filter;
    protected InterruptableDownloadManager downloadManager;
    protected Callback callback;
    protected EventBus eventBus;
    protected DocumentRepository repository;
    protected UserRepository userRepository;
    protected OfflineManager offlineManager;
    protected DownloadManager systemDownloadManager;
    protected CacheManager cacheManager;

    protected final Scheduler OBSERVER = AndroidSchedulers.mainThread();
    protected final Scheduler SUBSCRIBER = Schedulers.io();

    public DocumentsPresenter(DocFilter filter, EventBus eventBus,
                              DocumentRepository repository,
                              InterruptableDownloadManager downloadManager,
                              OfflineManager offlineManager,
                              CacheManager cacheManager,
                              DownloadManager systemDownloadManager,
                              UserRepository userRepository,
                              @NonNull Callback callback) {
        this.filter = filter;
        this.downloadManager = downloadManager;
        this.callback = callback;
        this.eventBus = eventBus;
        this.repository = repository;
        this.userRepository = userRepository;
        this.systemDownloadManager = systemDownloadManager;
        this.offlineManager = offlineManager;
        this.cacheManager = cacheManager;
    }

    public void setFilter(DocFilter filter) {
        this.filter = filter;
    }

    public void updateDocument(VkDocument document) {
        new UpdateDocument(SUBSCRIBER, eventBus, repository, document).execute();
    }

    public void downloadDocumentToDownloads(VkDocument document) {
        Uri uri = null;
        Timber.d("[download document] %s", document);
        Timber.d("[download document] ic_cached_green(%s), offline(%s)", document.isCached(), document.isOffline());
        try {
            if (document.isCached() || document.isOffline()) {
                Timber.d("[download document] already downloaded as local file");
                uri = Uri.parse(document.getPath());
            } else {
                Timber.d("[download document] not downloaded");
                uri = Uri.parse(document.url);
            }
        } catch (NullPointerException ignored) {
            return;
            // TODO: tell about invalid Uri back to UI
        }
        Timber.d("[download document] download Uri: %s", uri);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(document.title);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, document.title);
        request.setVisibleInDownloadsUi(true);
        Timber.d("[download document] enqueue request: %s", request);
        systemDownloadManager.enqueue(request);
    }

    public void getUserInfo() {
        userSubscriber = new GetUserInfoSubscriber();
        new GetUserInfo(
                OBSERVER,
                SUBSCRIBER,
                eventBus,
                userRepository
        ).execute(userSubscriber);
    }

    public void makeOffline(VkDocument document) {
        if (!document.isCached())
            new MakeOfflineDocument(
                    OBSERVER,
                    SUBSCRIBER,
                    eventBus,
                    offlineManager,
                    document).execute(new OfflineSubscriber());
        else {
            document.setOfflineType(VkDocument.OFFLINE);
            new UpdateDocument(SUBSCRIBER, eventBus, repository, document).execute();
            callback.onUpdatedDocument(document);
        }
    }

    public void undoMakeOffline(VkDocument document) {
        //TODO call cache manager
        if (document.isOffline()) {
            cacheManager.cacheFromOffline(document);
            callback.onUpdatedDocument(document);
        } else {
            new CancelDownloadingDocument(OBSERVER, SUBSCRIBER, eventBus, repository, downloadManager, document).execute();
            eventBus.removeEvent(MakeOfflineDocument.hashByDoc(document));
            callback.onUpdatedDocument(document);
        }
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

    public void retryDownloadDocument(VkDocument document) {
        List<DownloadRequest> requests = downloadManager.getQueue();
        for (DownloadRequest req: requests)
            if (req.getDocId() == document.getId()) {
                document.setRequest(req);
                req.resetError();//smth holy shit
                break;
            }
        //openDocument(document);
    }

    public void rename(VkDocument document, String newName) {
        new RenameDocument(OBSERVER, SUBSCRIBER, eventBus, repository, document, newName).execute();
    }

    public void delete(VkDocument document) {
        new DeleteDocument(OBSERVER, SUBSCRIBER, eventBus, repository, document).execute();
    }

    public void forceNetworkLoad() {
        networkSubscriber = new NetworkSubscriber();
        new NetworkDocuments(OBSERVER, SUBSCRIBER, eventBus, repository).
                execute(networkSubscriber);
    }

    public void getDocuments() {
        documentsSubscriber = new GetDocumentsSubscriber();
        new GetDocuments(OBSERVER, SUBSCRIBER, eventBus, repository).execute(documentsSubscriber);
    }

    @Override
    public void onStart() {
        /*Timber.d("on start get doc contains: " + eventBus.contains(GetDocuments.class));
        if (eventBus.contains(GetDocuments.class) && documentsSubscriber.isUnsubscribed()) {
            documentsSubscriber = new GetDocumentsSubscriber();
            eventBus.getEvent(GetDocuments.class).execute(documentsSubscriber);
        }*/

        if (eventBus.contains(NetworkDocuments.class) && networkSubscriber.isUnsubscribed()) {
            networkSubscriber = new NetworkSubscriber();
            eventBus.getEvent(NetworkDocuments.class).execute(networkSubscriber);
        }

        if (eventBus.contains(GetUserInfo.class) && userSubscriber.isUnsubscribed()) {
            userSubscriber = new GetUserInfoSubscriber();
            eventBus.getEvent(GetUserInfo.class).execute(userSubscriber);
        }
    }


    @Override
    public void onStop() {
        unsubscribeIfNot(documentsSubscriber);
        unsubscribeIfNot(networkSubscriber);
        unsubscribeIfNot(userSubscriber);
    }

    private void findDownloadRequests(List<VkDocument> documents) {
        List<DownloadRequest> requests = downloadManager.getQueue();
        for (VkDocument d: documents)
            for (DownloadRequest req: requests)
                if (req.getDocId() == d.getId()) {
                    d.setRequest(req);
                    break;
                }
    }

    public class GetUserInfoSubscriber extends DefaultSubscriber<VKApiUser> {
        @Override
        public void onNext(VKApiUser user) {
            callback.onUserInfoLoaded(user);
        }
    }

    public class GetDocumentsSubscriber extends DefaultSubscriber<List<VkDocument>> {
        @Override
        public void onNext(List<VkDocument> vkDocuments) {
            List<VkDocument> documents = filterList(vkDocuments);
            findDownloadRequests(documents);
            callback.onGetDocuments(copyVkDocumentsList(filterList(documents)));
            //eventBus.removeEvent(GetDocuments.class);
        }

        private List<VkDocument> copyVkDocumentsList(List<VkDocument> docs) {
            List<VkDocument> ret = new ArrayList<>();
            for (VkDocument x : docs)
                ret.add(x.copy());
            return ret;
        }

        @Override
        public void onError(Throwable e) {
            callback.onDatabaseError((Exception)e);
            eventBus.removeEvent(GetDocuments.class);
        }
    }

    public class NetworkSubscriber extends DefaultSubscriber<List<VkDocument>> {
        @Override
        public void onNext(List<VkDocument> vkDocuments) {
            List<VkDocument> list = filterList(vkDocuments);
            findDownloadRequests(list);
            callback.onNetworkDocuments(list);
            eventBus.removeEvent(NetworkDocuments.class);
            eventBus.removeEvent(GetDocuments.class);
        }

        @Override
        public void onError(Throwable e) {
            callback.onNetworkError((Exception) e);
            eventBus.removeEvent(NetworkDocuments.class);
        }
    }

    public class OfflineSubscriber extends DefaultSubscriber<VkDocument> {
        @Override
        public void onNext(VkDocument document) {
            callback.onUpdatedDocument(document);
            eventBus.removeEvent(MakeOfflineDocument.hashByDoc(document));//holy shit7 remove MakeOffline with this hash
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