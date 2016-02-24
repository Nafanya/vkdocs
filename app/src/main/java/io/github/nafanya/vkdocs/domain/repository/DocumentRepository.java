package io.github.nafanya.vkdocs.domain.repository;


import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

public interface DocumentRepository {
    List<VKApiDocument> getMyDocuments() throws Exception;
    void delete(VKApiDocument document) throws Exception;
    void add(VKApiDocument document) throws Exception;
    void synchronize() throws Exception;
}
