package io.github.nafanya.vkdocs.net.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.GetDocuments;
import io.github.nafanya.vkdocs.domain.interactor.UpdateAllDocuments;
import io.github.nafanya.vkdocs.domain.interactor.UpdateDocument;
import io.github.nafanya.vkdocs.domain.model.DocumentsInfo;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.base.CacheManager;
import io.github.nafanya.vkdocs.net.base.OfflineManager;
import io.github.nafanya.vkdocs.net.base.download.DownloadManager;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.utils.ThreadUtils;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CacheManagerImpl implements CacheManager {
    private DocumentRepository repository;
    private DownloadManager downloadManager;
    private EventBus eventBus;
    private String CACHE_ROOT;
    private long size;
    private final Scheduler IO_SCHEDULER = Schedulers.io();
    private volatile long currentTotalSize = 0;
    private volatile int currentFilesCached = 0;

    public CacheManagerImpl(
            EventBus eventBus,
            DocumentRepository repository,
            DownloadManager downloadManager,
            File cacheRoot, int size) {
        this.eventBus = eventBus;
        this.repository = repository;
        this.downloadManager = downloadManager;
        this.CACHE_ROOT = cacheRoot.getAbsolutePath() + File.separator;
        this.size = 1L * size * MB;
        validateAndRemoveFiles(size);
    }

    @Override
    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    @Override
    public void setSizeLimit(int size) {//in megabytes
        this.size = 1L * size * MB;
        validateAndRemoveFiles(size);
    }

    @Override
    public void cache(VkDocument document) {
        //String toPath = CACHE_ROOT + document.title;//+ "_" + document.getId();
        String toPath = CACHE_ROOT + document.getId();
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
                currentFilesCached++;
                currentTotalSize += document.size;
                if (currentTotalSize > size)
                    validateAndRemoveFiles(size);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        downloadManager.enqueue(request);

        document.setOfflineType(VkDocument.CACHE);
        document.setRequest(request);
        new UpdateDocument(IO_SCHEDULER, eventBus, repository, document).execute();
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

    private void validateAndRemoveFiles(long limitSize) {
        ThreadUtils.WorkerPool.execute(() -> {
            List<VkDocument> documents = GetDocuments.getDocuments(repository);
            List<CacheEntry> cacheEntries = new ArrayList<>();
            for (VkDocument d: documents)
                if (d.isCached()) {
                    if (d.getPath() == null) { //something holy shit, ebat moj huj
                        Timber.w("SOMETHING STRANGE: %s cached, but path is null", d.title);
                        d.setOfflineType(VkDocument.NONE);
                        new UpdateDocument(IO_SCHEDULER, eventBus, repository, d).execute();
                    } else
                        cacheEntries.add(new CacheEntry(d));
                }
            Collections.sort(cacheEntries);
            long currentSize = 0;

            long newCurrentSize = 0;
            int newFilesCached = 0;

            List<VkDocument> updDocs = new ArrayList<>();
            for (CacheEntry e: cacheEntries) {
                currentSize += e.size;
                if (!e.file.exists()) {
                    e.relatedDocument.setOfflineType(VkDocument.NONE);
                    e.relatedDocument.setPath(null);
                    updDocs.add(e.relatedDocument);
                } else if (currentSize > limitSize && currentSize - e.size >= limitSize) {
                    e.file.delete();
                    e.relatedDocument.setOfflineType(VkDocument.NONE);
                    e.relatedDocument.setPath(null);
                    updDocs.add(e.relatedDocument);
                } else {
                    newFilesCached++;
                    newCurrentSize += e.size;
                }
            }

            currentTotalSize = newCurrentSize;
            currentFilesCached = newFilesCached;
            Timber.d("current total size/memory = %d %d", currentFilesCached, currentTotalSize);
            new UpdateAllDocuments(IO_SCHEDULER, eventBus, repository, updDocs).execute();
        });
    }

    @Override
    public void cacheFromOffline(VkDocument document, OfflineManager offlineManager) {
        document.setOfflineType(VkDocument.CACHE);
        new UpdateDocument(IO_SCHEDULER, eventBus, repository, document).execute();
        offlineManager.removeFromOffline(document);

        currentTotalSize += document.size;
        currentFilesCached++;
        if (currentTotalSize > size)
            validateAndRemoveFiles(size);
    }

    @Override
    public void clear() {
        validateAndRemoveFiles(0);
    }

    @Override
    public int geSizeLimit() {
        return (int)(size / MB);
    }

    @Override
    public DocumentsInfo getCurrentDocumentsInfo() {
        return new DocumentsInfo(currentFilesCached, currentTotalSize);
    }

    @Override
    public void removeFromCache(VkDocument document) {
        currentFilesCached--;
        currentTotalSize -= document.size;
    }
}
