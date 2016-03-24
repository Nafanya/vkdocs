package io.github.nafanya.vkdocs.net.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.GetDocuments;
import io.github.nafanya.vkdocs.domain.interactor.UpdateDocument;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.base.CacheManager;
import io.github.nafanya.vkdocs.net.base.download.DownloadManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CacheManagerImpl implements CacheManager {
    private DocumentRepository repository;
    private DownloadManager downloadManager;
    private EventBus eventBus;
    private String CACHE_ROOT;
    private int size;
    private Scheduler SUBSCRIBER = Schedulers.io();

    public CacheManagerImpl(
            EventBus eventBus,
            DocumentRepository repository,
            DownloadManager downloadManager,
            File cacheRoot, int size) {
        this.eventBus = eventBus;
        this.repository = repository;
        this.downloadManager = downloadManager;
        this.CACHE_ROOT = cacheRoot.getAbsolutePath() + File.separator;
        this.size = size;
    }

    @Override
    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public void setSize(int size) {//in megabytes
        this.size = size;
        validateAndRemoveFiles(size);
    }

    @Override
    public void cache(VkDocument document) {
        //TODO remove old cached documents

        String toPath = CACHE_ROOT + document.title + "_" + document.getId();
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
                validateAndRemoveFiles(size);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        downloadManager.enqueue(request);

        document.setOfflineType(VkDocument.CACHE);
        document.setRequest(request);
        new UpdateDocument(SUBSCRIBER, eventBus, repository, document).execute();
    }

    private static final int MB = 1024 * 1024;
    private static class CacheEntry implements Comparable<CacheEntry> {
        public File file;
        public long modified;
        public long size;
        public VkDocument relatedDocument;

        public CacheEntry(VkDocument doc) {
            relatedDocument = doc;
            String path = doc.getPath();
            file = new File(path);
            modified = file.lastModified();
            size = file.length();
        }

        @Override
        public int compareTo(CacheEntry another) {
            long res =  modified - another.modified;
            if (res > 0)
                return -1;
            if (res < 0)
                return 1;
            return 0;
        }
    }

    private void validateAndRemoveFiles(int trimSize) {
        new GetDocuments(SUBSCRIBER, SUBSCRIBER, eventBus, repository).execute(new DefaultSubscriber<List<VkDocument>>() {
            @Override
            public void onNext(List<VkDocument> documents) {
                List<CacheEntry> cacheEntries = new ArrayList<>();
                for (VkDocument d: documents)
                    if (d.isCached())
                        cacheEntries.add(new CacheEntry(d));

                Collections.sort(cacheEntries);
                long currentSize = 0;
                long limitSize = 1L * trimSize * MB;
                for (CacheEntry e: cacheEntries) {
                    currentSize += e.size;
                    if (!e.file.exists()) {
                        e.relatedDocument.setOfflineType(VkDocument.NONE);
                        e.relatedDocument.setPath(null);
                        new UpdateDocument(SUBSCRIBER, eventBus, repository, e.relatedDocument).execute();
                    } else if (currentSize > limitSize && currentSize - e.size >= limitSize)
                        e.file.delete();
                }
            }
        });
    }

    @Override
    public void cacheFromOffline(VkDocument document) {
        document.setOfflineType(VkDocument.CACHE);
        new UpdateDocument(SUBSCRIBER, eventBus, repository, document).execute();
        validateAndRemoveFiles(size);
    }

    @Override
    public void clear() {
        validateAndRemoveFiles(0);
    }
}
