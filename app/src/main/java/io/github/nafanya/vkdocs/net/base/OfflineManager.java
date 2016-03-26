package io.github.nafanya.vkdocs.net.base;

import io.github.nafanya.vkdocs.domain.model.DocumentsInfo;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public interface OfflineManager {
    void offlineFromCache(VkDocument document, CacheManager cacheManager);

    interface OnPreparedCallback {
        void onPrepared(VkDocument document);
    }

    void clear();
    void makeOffline(VkDocument document, OnPreparedCallback onPreparedListener);
    DocumentsInfo getCurrentDocumentsInfo();
    void removeFromOffline(VkDocument document);
}
