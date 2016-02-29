package io.github.nafanya.vkdocs.data.database.mapper;


import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;

public class DocsMapper implements Mapper<VKDocumentEntity, VKApiDocument> {
    public DocsMapper() {

    }

    public VKApiDocument transform(VKDocumentEntity vkDocumentEntity) {
        VKApiDocument ret = new VKApiDocument();
        ret.id = vkDocumentEntity.getId();
        ret.title = vkDocumentEntity.getTitle();
        ret.owner_id = vkDocumentEntity.getOwnerId();
        ret.size = vkDocumentEntity.getSize();
        return ret;
    }

    public List<VKApiDocument> transform(List<VKDocumentEntity> vkDocumentEntities) {
        List<VKApiDocument> ret = new ArrayList<>();
        for (int i = 0; i < vkDocumentEntities.size(); ++i)
            ret.add(transform(vkDocumentEntities.get(i)));
        return ret;
    }

    @Override
    public VKDocumentEntity transformInv(VKApiDocument vkApiDoc) {
        VKDocumentEntity vk = new VKDocumentEntity();
        vk.setId(vkApiDoc.getId());
        vk.setTitle(vkApiDoc.title);
        vk.setOwnerId(vkApiDoc.owner_id);
        vk.setSize(vkApiDoc.size);
        return vk;
    }
}
