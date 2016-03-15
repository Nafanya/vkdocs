package io.github.nafanya.vkdocs.presentation.presenter.base.filter;

import java.io.Serializable;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class ExtDocFilter implements DocFilter {

    private VkDocument.ExtType[] types;

    public static final DocFilter ALL = new ExtDocFilter(
                    VkDocument.ExtType.TEXT,
                    VkDocument.ExtType.BOOK,
                    VkDocument.ExtType.ARCHIVE,
                    VkDocument.ExtType.GIF,
                    VkDocument.ExtType.IMAGE,
                    VkDocument.ExtType.AUDIO,
                    VkDocument.ExtType.VIDEO,
                    VkDocument.ExtType.UNKNOWN);

    public ExtDocFilter(VkDocument.ExtType... types) {
        this.types = types;
    }

    @Override
    public boolean filter(VkDocument doc) {
        for (VkDocument.ExtType type : types) {
            if (type == doc.getExtType()) {
                return true;
            }
        }
        return false;
    }
}