package io.github.nafanya.vkdocs.data.database.repository;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;

public interface DatabaseRepository {
    //Получает все существующие документы, сохраненные в базе
    List<VKDocumentEntity> getMyDocuments();
    //Получает все записи, в том числе с sync == DELETED
    List<VKDocumentEntity> getAllRecords();
    Mapper<VKDocumentEntity, VKApiDocument> getMapper();
    void delete(VKDocumentEntity document);
    void update(VKDocumentEntity document);
    void addAll(Iterable<VKDocumentEntity> list);
    void deleteAll(Iterable<VKDocumentEntity> list);
}
