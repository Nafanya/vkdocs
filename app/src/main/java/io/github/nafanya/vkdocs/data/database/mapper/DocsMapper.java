package io.github.nafanya.vkdocs.data.database.mapper;


import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocument;

public class DocsMapper implements Mapper<VKDocument, VKApiDocument> {
    public DocsMapper() {

    }

    public VKApiDocument transform(VKDocument vkDocument) {
        VKApiDocument ret = new VKApiDocument();
        ret.id = vkDocument.getId();
        ret.title = vkDocument.getTitle();
        ret.owner_id = vkDocument.getOwnerId();
        return ret;
    }

    public List<VKApiDocument> transform(List<VKDocument> vkDocuments) {
        List<VKApiDocument> ret = new ArrayList<>();
        for (int i = 0; i < vkDocuments.size(); ++i)
            ret.add(transform(vkDocuments.get(i)));
        return ret;
    }

    @Override
    public VKDocument transformInv(VKApiDocument vkApiDoc) {
        VKDocument vk = new VKDocument();
        vk.setId(vkApiDoc.getId());
        vk.setTitle(vkApiDoc.title);
        vk.setOwnerId(vkApiDoc.owner_id);
        return vk;
    }
}
