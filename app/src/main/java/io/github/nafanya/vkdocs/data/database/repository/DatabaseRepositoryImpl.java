package io.github.nafanya.vkdocs.data.database.repository;

import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.DocumentsDatabase;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity_Table;

public class DatabaseRepositoryImpl implements DatabaseRepository {
    private Mapper<VKDocumentEntity, VKApiDocument> mapper;

    public DatabaseRepositoryImpl(Mapper<VKDocumentEntity, VKApiDocument> mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<VKDocumentEntity> getMyDocuments() {
        return SQLite.select().from(VKDocumentEntity.class).
                where(VKDocumentEntity_Table.sync.notEq(VKDocumentEntity.DELETED)).orderBy(VKDocumentEntity_Table.id, false).queryList();
    }

    @Override
    public List<VKDocumentEntity> getAllRecords() {
        return SQLite.select().from(VKDocumentEntity.class).queryList();
    }

    @Override
    public Mapper<VKDocumentEntity, VKApiDocument> getMapper() {
        return mapper;
    }


    @Override
    public void delete(VKDocumentEntity document) {
        document.delete();
    }

    @Override
    public void update(VKDocumentEntity document) {
        document.update();
    }

    //Batch insert
    @Override
    public void addAll(final Iterable<VKDocumentEntity> list) {
        TransactionManager.transact(DocumentsDatabase.NAME, () -> {
            for (VKDocumentEntity doc : list)
                doc.save();
        });
    }

    //Batch delete
    @Override
    public void deleteAll(final Iterable<VKDocumentEntity> list) {
        TransactionManager.transact(DocumentsDatabase.NAME, new Runnable() {
            @Override
            public void run() {
                for (VKDocumentEntity doc : list)
                    doc.delete();
            }
        });
    }
}
