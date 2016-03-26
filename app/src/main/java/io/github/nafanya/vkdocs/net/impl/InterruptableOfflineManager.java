package io.github.nafanya.vkdocs.net.impl;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.GetDocuments;
import io.github.nafanya.vkdocs.domain.interactor.UpdateAllDocuments;
import io.github.nafanya.vkdocs.domain.interactor.UpdateDocument;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.model.DocumentsInfo;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.base.CacheManager;
import io.github.nafanya.vkdocs.net.base.InternetService;
import io.github.nafanya.vkdocs.net.base.OfflineManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.net.impl.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.utils.ThreadUtils;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class InterruptableOfflineManager implements OfflineManager, InternetService.InternetStateListener {
    private InterruptableDownloadManager downloadManager;
    private DocumentRepository repository;
    private EventBus eventBus;
    private String OFFLINE_ROOT;
    private final Scheduler IO_SCHEDULER = Schedulers.io();
    private volatile int currentTotalFiles = 0;
    private volatile long currentTotalSize = 0;

    public InterruptableOfflineManager(
            InternetService internetService,
            InterruptableDownloadManager downloadManager,
            DocumentRepository repository,
            EventBus eventBus,
            File offlineRoot) {
        internetService.addListener(this);
        this.downloadManager = downloadManager;
        this.repository = repository;
        this.eventBus = eventBus;
        this.OFFLINE_ROOT = offlineRoot.getAbsolutePath() + File.separator;

        List<DownloadRequest> reqs = downloadManager.getQueue();
        List<VkDocument> docs = repository.getMyDocuments();//TODO make async or faster
        for (DownloadRequest request : reqs) {
            VkDocument document = null;
            for (VkDocument d: docs)
                if (d.getId() == request.getDocId()) {
                    document = d;
                    break;
                }
            request.addListener(new CompleteDownloadingListener(document, request));
            if (internetService.hasInternetConnection())
                downloadManager.retry(request);
        }

        init();
    }

    private void init() {
        ThreadUtils.WorkerPool.execute(()->{
            List<VkDocument> docs = GetDocuments.getDocuments(repository);
            for (VkDocument d : docs)
                if (d.isOffline()) {
                    currentTotalFiles++;
                    currentTotalSize += d.size;
                }
        });
    }

    @Override
    public void onEnableWiFi() {

    }

    @Override
    public void onEnableMobile() {

    }

    @Override
    public void onEnableNetwork() {//TODO fix it
        Timber.d("on enable network");
        List<DownloadRequest> reqs = downloadManager.getQueue();
        for (DownloadRequest request : reqs) {
            request.resetError();
            downloadManager.retry(request);
        }
    }

    @Override
    public void offlineFromCache(VkDocument document, CacheManager cacheManager) {
        document.setOfflineType(VkDocument.OFFLINE);
        new UpdateDocument(Schedulers.io(), eventBus, repository, document).execute();
        cacheManager.removeFromCache(document);
    }

    @Override
    public void clear() {
        ThreadUtils.WorkerPool.execute(()->{
            Timber.d("[offline manager] clear");
            List<VkDocument> docs = GetDocuments.getDocuments(repository);
            List<VkDocument> updDocs = new ArrayList<>();
            int currentTotalFiles_ = 0;
            long currentTotalSize_ = 0;
            for (VkDocument d: docs)
                if (d.isOffline()) {
                    d.setOfflineType(VkDocument.NONE);
                    new File(d.getPath()).delete();
                    d.setPath(null);
                    updDocs.add(d);
                    currentTotalFiles_++;
                    currentTotalSize_ += d.size;
                }
            new UpdateAllDocuments(IO_SCHEDULER, eventBus, repository, docs).execute();

            currentTotalFiles -= currentTotalFiles_;
            currentTotalSize -= currentTotalSize_;
        });
    }

    @Override
    public DocumentsInfo getCurrentDocumentsInfo() {
        return new DocumentsInfo(currentTotalFiles, currentTotalSize);
    }

    @Override
    public void removeFromOffline(VkDocument document) {
        currentTotalFiles--;
        currentTotalSize -= document.size;
    }

    @Override
    public void makeOffline(VkDocument document, OnPreparedCallback callback) {
        String toPath = OFFLINE_ROOT + document.getId();//TODO fix it
        DownloadRequest request = new DownloadRequest(document.url, toPath);
        request.setDocId(document.getId());
        request.setTotalBytes(document.size);
        document.setOfflineType(VkDocument.OFFLINE);
        document.setRequest(request);
        callback.onPrepared(document);

        request.addListener(new CompleteDownloadingListener(document, request));

        //database operation
        downloadManager.enqueue(request);
        new UpdateDocument(Schedulers.io(), eventBus, repository, document).execute();
    }

    private class CompleteDownloadingListener implements DownloadRequest.RequestListener {
        private VkDocument document;
        private DownloadRequest request;

        public CompleteDownloadingListener(@NonNull VkDocument document,
                                           @NonNull DownloadRequest request) {
            this.document = document;
            this.request = request;
        }

        @Override
        public void onProgress(int percentage) {

        }

        @Override
        public void onComplete() {
            document.setPath(request.getDest());
            document.resetRequest();
            new UpdateDocument(Schedulers.io(), eventBus, repository, document).execute();//for design, caching in GetDocuments in future
            //request.removeListener(this);TODO fix it
            currentTotalFiles++;
            currentTotalSize += document.size;
        }

        @Override
        public void onError(Exception e) {

        }
    }
}
