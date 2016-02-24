package io.github.nafanya.vkdocs.data.database.repository;


import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.docer.Utils;
import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.mapper.DbToDomainMapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocument;

public class TestDatabaseRepository implements DatabaseRepository {
    private List<VKDocument> database = new ArrayList<>();
    private DbToDomainMapper mapper;

    public TestDatabaseRepository(DbToDomainMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<VKDocument> getMyDocuments() {
        try {
            Thread.sleep(Utils.randInt(20, 100));
            return database;
        } catch (InterruptedException ignore) {}
        return null;
    }

    @Override
    public Mapper<VKDocument, VKApiDocument> getMapper() {
        return mapper;
    }

    @Override
    public void delete(VKDocument document) {
        for (int i = 0; i < database.size(); ++i)
            if (database.get(i).getId() == document.getId()) {
                database.remove(i);
                break;
            }
    }

    @Override
    public void update(VKDocument document) {//TODO change it
        for (int i = 0; i < database.size(); ++i)
            if (database.get(i).getId() == document.getId()) {
                database.get(i).setTitle(document.getTitle());
                database.get(i).setSync(document.getSync());
                break;
            }
    }
}
