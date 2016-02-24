package io.github.nafanya.vkdocs.data.database.repository;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocument;

public interface DatabaseRepository {
    List<VKDocument> getMyDocuments();
    Mapper<VKDocument, VKApiDocument> getMapper();
    void delete(VKDocument document);
    void update(VKDocument document);
}
