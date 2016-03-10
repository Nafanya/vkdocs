package io.github.nafanya.vkdocs.presentation.presenter.base.filter;

import java.io.Serializable;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class OfflineDocFilter implements DocFilter {
    private VkDocument.ExtType[] types;

    public OfflineDocFilter(VkDocument.ExtType... types) {
        this.types = types;
    }

    @Override
    public boolean filter(VkDocument doc) {
        for (VkDocument.ExtType type: types)
            if (type == doc.getExtType() && doc.isOffline() && doc.isOfflineInProgress())
                return true;
        return false;
    }
}
