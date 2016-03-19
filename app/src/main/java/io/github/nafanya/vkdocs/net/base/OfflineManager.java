package io.github.nafanya.vkdocs.net.base;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

public interface OfflineManager {
    void makeOffline(VkDocument document, String toPath);
}
