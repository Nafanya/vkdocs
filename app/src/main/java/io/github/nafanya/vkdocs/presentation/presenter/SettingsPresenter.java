package io.github.nafanya.vkdocs.presentation.presenter;

import io.github.nafanya.vkdocs.domain.interactor.GetDocuments;
import io.github.nafanya.vkdocs.domain.model.DocumentsInfo;
import io.github.nafanya.vkdocs.net.base.CacheManager;
import io.github.nafanya.vkdocs.net.base.OfflineManager;
import io.github.nafanya.vkdocs.presentation.presenter.base.BasePresenter;

public class SettingsPresenter extends BasePresenter {
    private CacheManager cacheManager;
    private OfflineManager offlineManager;

    public SettingsPresenter(OfflineManager offlineManager, CacheManager cacheManager) {
        this.offlineManager = offlineManager;
        this.cacheManager = cacheManager;
    }

    public int getCacheSize() {
        return cacheManager.getSize();
    }

    public void clearCache() {
        cacheManager.clear();
    }

    public void clearOffline() {
        offlineManager.clear();
    }

    public void setCacheSize(int size) {
        cacheManager.setSize(size);
    }

    public DocumentsInfo getOfflineInfo() {
        return offlineManager.getCurrentDocumentsInfo();
    }

    public DocumentsInfo getCacheInfo() {
        return cacheManager.getCurrentDocumentsInfo();
    }

    public int getTotalFiles() {
        return GetDocuments.size();
    }
}
