package io.github.nafanya.vkdocs.data;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepository;
import io.github.nafanya.vkdocs.data.net.NetworkRepository;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;

public class DocumentRepositoryImpl implements DocumentRepository {
    private static final Comparator<VKDocumentEntity> COMPARATOR = (lhs, rhs) -> lhs.getId() - rhs.getId();

    private DatabaseRepository databaseRepository;
    private NetworkRepository networkRepository;
    private Mapper<VKDocumentEntity, VkDocument> dbMapper;
    private Mapper<VKApiDocument, VkDocument> netMapper;

    public DocumentRepositoryImpl(DatabaseRepository databaseRepository,
                                  NetworkRepository networkRepository) {
        this.databaseRepository = databaseRepository;
        this.networkRepository = networkRepository;
        this.dbMapper = databaseRepository.getMapper();
        this.netMapper = networkRepository.getMapper();
    }

    @Override
    public List<VkDocument> getMyDocuments() {
        return dbMapper.transform(databaseRepository.getMyDocuments());
    }

    @Override
    public void delete(final VkDocument document) {
        try {
            VKDocumentEntity doc = dbMapper.transformInv(document);
            doc.setSync(VKDocumentEntity.DELETED);
            databaseRepository.update(doc);
            networkRepository.delete(document);
        } catch (Exception e) {
            //документ удалится в следующий раз
        }
    }

    @Override
    public void add(VKApiDocument document) throws Exception {
        throw new UnsupportedOperationException("Unimplemented exception");
    }

    @Override
    public void update(VkDocument document) {
        databaseRepository.update(dbMapper.transformInv(document));
    }

    @Override
    public void synchronize() throws Exception {
        List<VkDocument> netDocs = netMapper.transform(networkRepository.getMyDocuments());
        Map<Integer, Integer> syncState = new TreeMap<>();
        Set<VKDocumentEntity> deleteDbDocs = new TreeSet<>(COMPARATOR);

        List<VKDocumentEntity> dbDocs = databaseRepository.getAllRecords();
        for (int i = 0; i < dbDocs.size(); ++i) {
            syncState.put(dbDocs.get(i).getId(), dbDocs.get(i).getSync());
            deleteDbDocs.add(dbDocs.get(i));
        }
        List<VKDocumentEntity> newDocuments = new ArrayList<>();
        VKDocumentEntity dummyDoc = new VKDocumentEntity();

        for (int i = 0; i < netDocs.size(); ++i) {
            VkDocument cur = netDocs.get(i);
            Integer state =  syncState.get(cur.getId());
            dummyDoc.setId(cur.getId());
            deleteDbDocs.remove(dummyDoc);

            if (state == null)
                newDocuments.add(dbMapper.transformInv(cur));
            else if (state == VKDocumentEntity.DELETED) {
                try {
                    networkRepository.delete(cur);
                } catch (Exception ignore) {
                    //этот документ удалится в следующий раз
                }
            }
        }

        databaseRepository.addAll(newDocuments);//batch synchronously insert
        databaseRepository.deleteAll(deleteDbDocs);//batch synchronously delete
    }
}
