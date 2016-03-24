package io.github.nafanya.vkdocs.net.base;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

public interface OfflineManager {
    interface OnPreparedCallback {
        void onPrepared(VkDocument document);
    }
    void makeOffline(VkDocument document, OnPreparedCallback onPreparedListener);
}
