package io.github.nafanya.vkdocs.presentation.ui.adapters.base;

import com.vk.sdk.api.model.VKApiDocument;

public interface CommonItemEventListener {
    void onClick(int position, VKApiDocument document);
}
