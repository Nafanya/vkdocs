package io.github.nafanya.vkdocs.net.base;

import io.github.nafanya.vkdocs.domain.model.DocumentsInfo;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.base.download.DownloadManager;

public interface CacheManager {
    DownloadManager getDownloadManager();
    void cache(VkDocument document);
    void cacheFromOffline(VkDocument document, OfflineManager offlineManager);
    void clear();
    int getSize();
    void setSize(int sizeLimit);
    DocumentsInfo getCurrentDocumentsInfo();

    void removeFromCache(VkDocument document);
}
