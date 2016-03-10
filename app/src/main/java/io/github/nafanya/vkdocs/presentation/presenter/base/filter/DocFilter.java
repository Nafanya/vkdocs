package io.github.nafanya.vkdocs.presentation.presenter.base.filter;

import java.io.Serializable;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

public interface DocFilter extends Serializable {
    boolean filter(VkDocument doc);
}
