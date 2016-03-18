package io.github.nafanya.vkdocs.data.net;


import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public interface NetworkRepository {
    List<VKApiDocument> getMyDocuments() throws Exception;
    void delete(VkDocument document) throws Exception;
    void rename(VkDocument document) throws Exception;
    Mapper<VKApiDocument, VkDocument> getMapper();
}
