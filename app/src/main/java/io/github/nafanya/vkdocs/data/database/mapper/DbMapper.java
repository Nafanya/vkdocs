package io.github.nafanya.vkdocs.data.database.mapper;


import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class DbMapper extends Mapper<VKDocumentEntity, VkDocument> {
    public DbMapper() {

    }

    public VkDocument transform(VKDocumentEntity vkDocumentEntity) {
        VkDocument ret = new VkDocument();
        ret.id = vkDocumentEntity.getId();
        ret.title = vkDocumentEntity.getTitle();
        ret.owner_id = vkDocumentEntity.getOwnerId();
        ret.size = vkDocumentEntity.getSize();
        return ret;
    }

    @Override
    public VKDocumentEntity transformInv(VkDocument vkApiDoc) {
        VKDocumentEntity vk = new VKDocumentEntity();
        vk.setId(vkApiDoc.getId());
        vk.setTitle(vkApiDoc.title);
        vk.setOwnerId(vkApiDoc.owner_id);
        vk.setSize(vkApiDoc.size);
        return vk;
    }
}
