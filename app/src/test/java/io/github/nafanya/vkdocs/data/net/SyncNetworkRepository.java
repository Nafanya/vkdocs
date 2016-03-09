package io.github.nafanya.vkdocs.data.net;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.nafanya.vkdocs.Utils;
import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.ServerSideNetworkRepository;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.InternetService;

/**
 * Created by pva701 on 24.02.16.
 */
public class SyncNetworkRepository implements NetworkRepository, ServerSideNetworkRepository {

    private Set<VKApiDocument> server = new TreeSet<>(new Comparator<VKApiDocument>() {
        @Override
        public int compare(VKApiDocument lhs, VKApiDocument rhs) {
            return lhs.getId() - rhs.getId();
        }
    });

    public final int OPERATION_TIME;
    private InternetService internetService;

    public SyncNetworkRepository(int size, int operationTime, InternetService internetService) {
        OPERATION_TIME = operationTime;
        for (int i = 0; i < size; ++i) {
            VKApiDocument doc = Utils.randVkApiDocument();
            server.add(doc);
        }

        this.internetService = internetService;
    }

    @Override
    public List<VKApiDocument> getMyDocuments() {
        try {
            int t = Utils.randInt(50, OPERATION_TIME);
            Thread.sleep(t);
            List<VKApiDocument> ret = new ArrayList<>();
            for (VKApiDocument d : server)
                ret.add(d);
            return ret;
        } catch (InterruptedException ignore) {
            ignore.printStackTrace();
        }
        return null;
    }


    @Override
    public void delete(VKApiDocument document) throws Exception {
        if (internetService.hasInternetConnection())
            server.remove(document);
        else
            throw new Exception("No internet connection!");
    }

    @Override
    public Mapper<VKApiDocument, VkDocument> getMapper() {
        return null;
    }

    @Override
    public void addSS(VKApiDocument doc) {
        server.add(doc);
    }

    @Override
    public void deleteSS(VKApiDocument doc) {
        server.remove(doc);
    }
}
