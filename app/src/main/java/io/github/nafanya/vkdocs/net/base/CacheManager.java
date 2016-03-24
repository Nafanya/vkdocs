package io.github.nafanya.vkdocs.net.base;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.base.download.DownloadManager;

public interface CacheManager {
    DownloadManager getDownloadManager();
    void cache(VkDocument document);
    void cacheFromOffline(VkDocument document);
    void clear();
}
