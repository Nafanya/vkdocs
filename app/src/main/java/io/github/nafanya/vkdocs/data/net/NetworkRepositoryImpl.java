package io.github.nafanya.vkdocs.data.net;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDocument;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.exceptions.VKException;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.base.InternetService;
import io.github.nafanya.vkdocs.utils.Utils;
import timber.log.Timber;


public class NetworkRepositoryImpl implements NetworkRepository {

    private Mapper<VKApiDocument, VkDocument> mapper;

    public NetworkRepositoryImpl(Mapper<VKApiDocument, VkDocument> mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<VKApiDocument> getMyDocuments() throws VKException {
        VKResponse response = Utils.syncVKRequest(VKApi.docs().get());
        List<VKApiDocument> documents = new ArrayList<>();
        try {
            JSONArray jsonArray = response.json.getJSONObject("response").getJSONArray("items");
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                VKApiDocument document = new VKApiDocument(jsonArray.getJSONObject(i));
                documents.add(document);
            }

        } catch (Exception ignore) {
        }
        return documents;
    }

    //async deleteAsync
    @Override
    public void deleteAsync(final VkDocument document) throws VKException {
        //Utils.syncVKRequest(VKApi.docs().getDeleteRequest(document.owner_id, document.id));
        VKApi.docs().getDeleteRequest(document.owner_id, document.id).start();
    }

    @Override
    public void rename(VkDocument document) {
        Timber.d(document.owner_id + " " + document.id + " " + document.title);
        VKApi.docs().getEdit(document.owner_id, document.id, document.title).start();
    }

    @Override
    public Mapper<VKApiDocument, VkDocument> getMapper() {
        return mapper;
    }
}
