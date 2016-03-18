package io.github.nafanya.vkdocs.data.database.repository;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public interface DatabaseRepository {
    //Получает все существующие документы, сохраненные в базе
    List<VKDocumentEntity> getMyDocuments();
    //Получает все записи, в том числе с sync == DELETED
    List<VKDocumentEntity> getAllRecords();
    Mapper<VKDocumentEntity, VkDocument> getMapper();
    void delete(VKDocumentEntity document);
    void update(VKDocumentEntity document);
    void addAll(Iterable<VKDocumentEntity> list);
    void deleteAll(Iterable<VKDocumentEntity> list);
    void updateAll(List<VKDocumentEntity> updatedDocuments);
}
