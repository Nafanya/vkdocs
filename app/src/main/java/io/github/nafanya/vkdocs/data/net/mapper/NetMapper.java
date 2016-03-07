package io.github.nafanya.vkdocs.data.net.mapper;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class NetMapper extends Mapper<VKApiDocument, VkDocument> {
    @Override
    public VkDocument transform(VKApiDocument x) {
        return new VkDocument(x);
    }

    @Override
    public VKApiDocument transformInv(VkDocument x) {
        return null;
    }
}
