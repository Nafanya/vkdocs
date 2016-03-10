package io.github.nafanya.vkdocs.presentation.presenter.base.filter;

import java.io.Serializable;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class ExtDocFilter implements DocFilter {
    private VkDocument.ExtType[] types;
    public ExtDocFilter(VkDocument.ExtType... types) {
        this.types = types;
    }

    @Override
    public boolean filter(VkDocument doc) {
        for (VkDocument.ExtType type: types)
            if (type == doc.getExtType())
                return true;
        return false;
    }
}