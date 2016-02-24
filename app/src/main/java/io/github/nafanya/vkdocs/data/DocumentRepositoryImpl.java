package io.github.nafanya.vkdocs.data;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.github.nafanya.vkdocs.data.database.model.VKDocument;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepository;
import io.github.nafanya.vkdocs.data.exceptions.VKException;
import io.github.nafanya.vkdocs.data.net.NetworkRepository;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;

public class DocumentRepositoryImpl implements DocumentRepository {
    private static final Comparator<VKDocument> COMPARATOR = new Comparator<VKDocument>() {
        @Override
        public int compare(VKDocument lhs, VKDocument rhs) {
            return lhs.getId() - rhs.getId();
        }
    };

    private DatabaseRepository databaseRepository;
    private NetworkRepository networkRepository;
    private Mapper<VKDocument, VKApiDocument> mapper;

    public DocumentRepositoryImpl(DatabaseRepository databaseRepository,
                                  NetworkRepository networkRepository) {
        this.databaseRepository = databaseRepository;
        this.networkRepository = networkRepository;
        this.mapper = databaseRepository.getMapper();
    }

    @Override
    public List<VKApiDocument> getMyDocuments() {
        return mapper.transform(databaseRepository.getMyDocuments());
    }

    @Override
    public void delete(final VKApiDocument document) throws Exception {
        try {
            networkRepository.delete(document);
            databaseRepository.delete(mapper.transformInv(document));
        } catch (VKException e) {
            VKDocument doc = mapper.transformInv(document);
            doc.setSync(VKDocument.DELETED);
            databaseRepository.update(doc);
        }
    }

    @Override
    public void add(VKApiDocument document) throws Exception {
        throw new UnsupportedOperationException("Unimplemented exception");
    }

    @Override
    public void synchronize() throws Exception {//TODO batch insert here
        List<VKApiDocument> netDocs = networkRepository.getMyDocuments();
        Map<Integer, Integer> syncState = new HashMap<>();
        Set<VKDocument> deleteDbDocs = new TreeSet<>(COMPARATOR);

        List<VKDocument> dbDocs = databaseRepository.getMyDocuments();
        for (int i = 0; i < dbDocs.size(); ++i) {
            syncState.put(dbDocs.get(i).getId(), dbDocs.get(i).getSync());
            deleteDbDocs.add(dbDocs.get(i));
        }

        VKDocument dummyDoc = new VKDocument();
        for (int i = 0; i < netDocs.size(); ++i) {
            VKApiDocument cur = netDocs.get(i);
            Integer state =  syncState.get(cur.getId());
            dummyDoc.setId(cur.getId());
            deleteDbDocs.remove(dummyDoc);

            if (state == null)
                mapper.transformInv(cur).insert();
            else if (state == VKDocument.DELETED) {
                //TODO delete document from server
            }
        }

        for (VKDocument deleteDbDoc : deleteDbDocs)
            deleteDbDoc.delete();
    }
}
