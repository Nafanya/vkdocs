package io.github.nafanya.vkdocs.data.database.repository;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.nafanya.vkdocs.Utils;
import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.mapper.DbMapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class InMemoryDatabaseRepository implements DatabaseRepository {
    private Set<VKDocumentEntity> database = new TreeSet<>(new Comparator<VKDocumentEntity>() {
        @Override
        public int compare(VKDocumentEntity lhs, VKDocumentEntity rhs) {
            return lhs.getId() - rhs.getId();
        }
    });

    public final int OPERATION_TIME;
    private DbMapper mapper;

    public InMemoryDatabaseRepository(DbMapper mapper, int operationTime) {
        OPERATION_TIME = operationTime;
        this.mapper = mapper;
    }

    @Override
    public List<VKDocumentEntity> getMyDocuments() {
        try {
            Thread.sleep(Utils.randInt(20, OPERATION_TIME));
            List<VKDocumentEntity> docs = new ArrayList<>();
            for (VKDocumentEntity d: database)
                if (d.getSync() == VKDocumentEntity.SYNCHRONIZED)
                    docs.add(d);
            return docs;
        } catch (InterruptedException ignore) {}
        return null;
    }

    @Override
    public List<VKDocumentEntity> getAllRecords() {
        try {
            Thread.sleep(Utils.randInt(20, OPERATION_TIME));
            List<VKDocumentEntity> docs = new ArrayList<>();
            for (VKDocumentEntity d: database)
                docs.add(d);
            return docs;
        } catch (InterruptedException ignore) {}
        return null;
    }

    @Override
    public Mapper<VKDocumentEntity, VkDocument> getMapper() {
        return mapper;
    }

    @Override
    public void delete(VKDocumentEntity document) {
        database.remove(document);
    }

    @Override
    public void update(VKDocumentEntity document) {
        if (!database.contains(document))
            throw new AssertionError("No element!");
        database.remove(document);
        database.add(document);
    }

    @Override
    public void addAll(Iterable<VKDocumentEntity> list) {
        try {
            Thread.sleep(Utils.randInt(20, OPERATION_TIME));
            for (VKDocumentEntity d : list)
                if (database.contains(d))
                    throw new AssertionError("Already contains element! Element = " + d.getId() + " title = " + d.getTitle());
                else
                    database.add(d);
        } catch (InterruptedException ignore) {}
    }

    @Override
    public void deleteAll(Iterable<VKDocumentEntity> list) {
        try {
            Thread.sleep(Utils.randInt(20, OPERATION_TIME));
            for (VKDocumentEntity d : list)
                    database.remove(d);
        } catch (InterruptedException ignore) {}
    }

    @Override
    public void updateAll(List<VKDocumentEntity> updatedDocuments) {
        throw new RuntimeException("Unimplemented");
    }
}
