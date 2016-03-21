package io.github.nafanya.vkdocs.net.impl;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.UpdateDocument;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.base.InternetService;
import io.github.nafanya.vkdocs.net.base.OfflineManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.net.impl.download.InterruptableDownloadManager;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class InterruptableOfflineManager implements OfflineManager, InternetService.InternetStateListener {
    private InterruptableDownloadManager downloadManager;
    private DocumentRepository repository;
    private EventBus eventBus;

    public InterruptableOfflineManager(
            InternetService internetService,
            InterruptableDownloadManager downloadManager,
            DocumentRepository repository,
            EventBus eventBus) {
        internetService.addListener(this);
        this.downloadManager = downloadManager;
        this.repository = repository;
        this.eventBus = eventBus;

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
    }

    @Override
    public void onEnableWiFi() {

    }

    @Override
    public void onEnableMobile() {

    }

    @Override
    public void onEnableNetwork() {
        Timber.d("on enable network");
        List<DownloadRequest> reqs = downloadManager.getQueue();
        for (DownloadRequest request : reqs) {
            request.resetError();
            downloadManager.retry(request);
        }
    }


    @Override
    public void makeOffline(VkDocument document, String toPath, OnPreparedCallback callback) {
        DownloadRequest request = new DownloadRequest(document.url, toPath);
        request.setDocId(document.getId());
        request.setTotalBytes(document.size);
        document.setOfflineType(VkDocument.OFFLINE);
        document.setRequest(request);
        callback.onPrepared(document);

        request.addListener(new CompleteDownloadingListener(document, request));
        //database operation
        downloadManager.enqueue(request);
        repository.update(document);
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
            new UpdateDocument(Schedulers.io(), eventBus, repository, document).execute();
            //request.removeListener(this);TODO fix it
        }

        @Override
        public void onError(Exception e) {

        }
    }
}
