package io.github.nafanya.vkdocs.data.database.repository;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocument;
public interface DatabaseRepository {
    //Получает все существующие документы, сохраненные в базе
    List<VKDocument> getMyDocuments();
    //Получает все записи, в том числе с sync == DELETED
    List<VKDocument> getAllRecords();
    Mapper<VKDocument, VKApiDocument> getMapper();
    void delete(VKDocument document);
    void update(VKDocument document);
    void addAll(Iterable<VKDocument> list);
    void deleteAll(Iterable<VKDocument> list);
}
