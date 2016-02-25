package io.github.nafanya.vkdocs.data.database.repository;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.nafanya.vkdocs.Utils;
import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.mapper.DocsMapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocument;

public class InMemoryDatabaseRepository implements DatabaseRepository {
    private Set<VKDocument> database = new TreeSet<>(new Comparator<VKDocument>() {
        @Override
        public int compare(VKDocument lhs, VKDocument rhs) {
            return lhs.getId() - rhs.getId();
        }
    });

    public final int OPERATION_TIME;
    private DocsMapper mapper;

    public InMemoryDatabaseRepository(DocsMapper mapper, int operationTime) {
        OPERATION_TIME = operationTime;
        this.mapper = mapper;
    }

    @Override
    public List<VKDocument> getMyDocuments() {
        try {
            Thread.sleep(Utils.randInt(20, OPERATION_TIME));
            List<VKDocument> docs = new ArrayList<>();
            for (VKDocument d: database)
                if (d.getSync() == VKDocument.SYNCHRONIZED)
                    docs.add(d);
            return docs;
        } catch (InterruptedException ignore) {}
        return null;
    }

    @Override
    public List<VKDocument> getAllRecords() {
        try {
            Thread.sleep(Utils.randInt(20, OPERATION_TIME));
            List<VKDocument> docs = new ArrayList<>();
            for (VKDocument d: database)
                docs.add(d);
            return docs;
        } catch (InterruptedException ignore) {}
        return null;
    }

    @Override
    public Mapper<VKDocument, VKApiDocument> getMapper() {
        return mapper;
    }

    @Override
    public void delete(VKDocument document) {
        database.remove(document);
    }

    @Override
    public void update(VKDocument document) {
        if (!database.contains(document))
            throw new AssertionError("No element!");
        database.remove(document);
        database.add(document);
    }

    @Override
    public void addAll(Iterable<VKDocument> list) {
        try {
            Thread.sleep(Utils.randInt(20, OPERATION_TIME));
            for (VKDocument d : list)
                if (database.contains(d))
                    throw new AssertionError("Already contains element! Element = " + d.getId() + " title = " + d.getTitle());
                else
                    database.add(d);
        } catch (InterruptedException ignore) {}
    }

    @Override
    public void deleteAll(Iterable<VKDocument> list) {
        try {
            Thread.sleep(Utils.randInt(20, OPERATION_TIME));
            for (VKDocument d : list)
                    database.remove(d);
        } catch (InterruptedException ignore) {}
    }
}
