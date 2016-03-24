package io.github.nafanya.vkdocs.net.impl;

import java.io.File;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.GetDocuments;
import io.github.nafanya.vkdocs.domain.interactor.UpdateDocument;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.base.CacheManager;
import io.github.nafanya.vkdocs.net.base.download.DownloadManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CacheManagerImpl implements CacheManager {
    private DocumentRepository repository;
    private DownloadManager downloadManager;
    private EventBus eventBus;
    private String CACHE_PATH;

    public CacheManagerImpl(EventBus eventBus, DocumentRepository repository, DownloadManager downloadManager, File cacheRoot) {
        this.eventBus = eventBus;
        this.repository = repository;
        this.downloadManager = downloadManager;
        this.CACHE_PATH = cacheRoot.getAbsolutePath() + File.separator;
    }

    @Override
    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    @Override
    public void cache(VkDocument document) {
        //TODO remove old cached documents

        String toPath = CACHE_PATH + document.getId();
        DownloadRequest request = new DownloadRequest(document.url, toPath);
        request.setDocId(document.getId());
        request.setTotalBytes(document.size);

        request.addListener(new DownloadRequest.RequestListener() {
            @Override
            public void onProgress(int percentage) {

            }

            @Override
            public void onComplete() {
                Timber.d("ON COMPLETE CACHE " + document);
                document.setPath(request.getDest());
                document.resetRequest();
                new UpdateDocument(Schedulers.io(), eventBus, repository, document).execute();//for design, caching in GetDocuments in future
                //TODO remove listener here, jobana v rot. or no
            }

            @Override
            public void onError(Exception e) {

            }
        });
        downloadManager.enqueue(request);

        document.setOfflineType(VkDocument.CACHE);
        document.setRequest(request);
        new UpdateDocument(Schedulers.io(), eventBus, repository, document).execute();
    }

    @Override
    public void cacheFromOffline(VkDocument document) {

    }
}
