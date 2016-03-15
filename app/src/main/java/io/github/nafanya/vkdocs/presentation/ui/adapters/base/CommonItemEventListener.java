package io.github.nafanya.vkdocs.presentation.ui.adapters.base;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

public interface CommonItemEventListener {
    void onClick(int position, VkDocument document);
    void onClickContextMenu(int position, VkDocument document);
}
