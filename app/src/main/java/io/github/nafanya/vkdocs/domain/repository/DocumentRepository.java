package io.github.nafanya.vkdocs.domain.repository;


import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

public interface DocumentRepository {
    List<VkDocument> getMyDocuments() throws Exception;
    void delete(VkDocument document) throws Exception;
    void add(VKApiDocument document) throws Exception;
    void synchronize() throws Exception;
}
