package io.github.nafanya.vkdocs.data.database.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.mapper.DbToDomainMapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocument;

public class DatabaseRepositoryImpl implements DatabaseRepository {
    private DbToDomainMapper mapper;

    public DatabaseRepositoryImpl(DbToDomainMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<VKDocument> getMyDocuments() {
        return SQLite.select().from(VKDocument.class).queryList();
    }

    @Override
    public Mapper<VKDocument, VKApiDocument> getMapper() {
        return mapper;
    }

    //TODO remove items with flag DELETED
    @Override
    public void delete(VKDocument document) {
        document.delete();
    }

    @Override
    public void update(VKDocument document) {
        document.update();
    }
}
