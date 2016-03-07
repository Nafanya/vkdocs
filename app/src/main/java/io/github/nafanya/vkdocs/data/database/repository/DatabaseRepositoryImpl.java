package io.github.nafanya.vkdocs.data.database.repository;

import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.DocumentsDatabase;
import io.github.nafanya.vkdocs.data.database.model.DownloadRequestEntity;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity_Table;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class DatabaseRepositoryImpl implements DatabaseRepository {
    private Mapper<VKDocumentEntity, VkDocument> mapper;

    public DatabaseRepositoryImpl(Mapper<VKDocumentEntity, VkDocument> mapper) {
        this.mapper = mapper;
    }

    //TODO fix it or no
    @Override
    public List<VKDocumentEntity> getMyDocuments() {
        List<VKDocumentEntity> all = SQLite.select().from(VKDocumentEntity.class).
                where(VKDocumentEntity_Table.sync.notEq(VKDocumentEntity.DELETED)).
                orderBy(VKDocumentEntity_Table.id, false).
                queryList();

        List<DownloadRequestEntity> requests =
                SQLite.select().from(DownloadRequestEntity.class).queryList();
        for (VKDocumentEntity x: all)
            for (DownloadRequestEntity req: requests)
                if (x.getId() == req.getDocId()) {
                    x.setDownloadRequest(req);
                    break;
                }
        return all;
    }

    @Override
    public List<VKDocumentEntity> getAllRecords() {
        return SQLite.select().from(VKDocumentEntity.class).queryList();
    }

    @Override
    public Mapper<VKDocumentEntity, VkDocument> getMapper() {
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
        TransactionManager.transact(DocumentsDatabase.NAME, () -> {
            for (VKDocumentEntity doc : list)
                doc.delete();
        });
    }
}
