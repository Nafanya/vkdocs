package io.github.nafanya.vkdocs.data.database.repository;

import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.DocumentsDatabase;
import io.github.nafanya.vkdocs.data.database.model.VKDocument;
import io.github.nafanya.vkdocs.data.database.model.VKDocument_Table;

public class DatabaseRepositoryImpl implements DatabaseRepository {
    private Mapper<VKDocument, VKApiDocument> mapper;

    public DatabaseRepositoryImpl(Mapper<VKDocument, VKApiDocument> mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<VKDocument> getMyDocuments() {
        return SQLite.select().from(VKDocument.class).
                where(VKDocument_Table.sync.notEq(VKDocument.DELETED)).orderBy(VKDocument_Table.id, false).queryList();
    }

    @Override
    public List<VKDocument> getAllRecords() {
        return SQLite.select().from(VKDocument.class).queryList();
    }

    @Override
    public Mapper<VKDocument, VKApiDocument> getMapper() {
        return mapper;
    }


    @Override
    public void delete(VKDocument document) {
        document.delete();
    }

    @Override
    public void update(VKDocument document) {
        document.update();
    }

    //Batch insert
    @Override
    public void addAll(final Iterable<VKDocument> list) {
        TransactionManager.transact(DocumentsDatabase.NAME, new Runnable() {
            @Override
            public void run() {
                for (VKDocument doc : list)
                    doc.save();
            }
        });
    }

    //Batch delete
    @Override
    public void deleteAll(final Iterable<VKDocument> list) {
        TransactionManager.transact(DocumentsDatabase.NAME, new Runnable() {
            @Override
            public void run() {
                for (VKDocument doc : list)
                    doc.delete();
            }
        });
    }
}
