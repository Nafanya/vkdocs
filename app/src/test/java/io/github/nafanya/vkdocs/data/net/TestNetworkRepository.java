package io.github.nafanya.vkdocs.data.net;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.nafanya.vkdocs.Utils;

/**
 * Created by pva701 on 24.02.16.
 */
public class TestNetworkRepository implements NetworkRepository {

    private List<VKApiDocument> server = new ArrayList<>();

    public TestNetworkRepository() {}

    public TestNetworkRepository(int size) {
        Set<Integer> tree = new TreeSet<>();
        while (tree.size() < size)
            tree.add(Utils.randInt());

        for (Integer id : tree) {
            VKApiDocument doc = new VKApiDocument();
            doc.title = "title" + id;
            doc.id = id;
            server.add(doc);
        }
    }

    @Override
    public List<VKApiDocument> getMyDocuments() throws Exception {
        int t = Utils.randInt(100, 500);
        Thread.sleep(t);
        return server;
    }


    @Override
    public void delete(VKApiDocument document) throws Exception {
        for (int i = 0; i < server.size(); ++i)
            if (server.get(i).getId() == document.getId()) {
                server.remove(i);
                break;
            }
    }
}
