package io.github.nafanya.vkdocs.data.database.mapper;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.DownloadRequestEntity;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;
import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class DbMapper extends Mapper<VKDocumentEntity, VkDocument> {
    private Mapper<DownloadRequestEntity, DownloadRequest> mapper;

    public DbMapper(Mapper<DownloadRequestEntity, DownloadRequest> mapper) {
        this.mapper = mapper;
    }

    public VkDocument transform(VKDocumentEntity vkDocumentEntity) {
        VkDocument ret = new VkDocument();
        ret.id = vkDocumentEntity.getId();
        ret.title = vkDocumentEntity.getTitle();
        ret.owner_id = vkDocumentEntity.getOwnerId();
        ret.size = vkDocumentEntity.getSize();
        ret.url = vkDocumentEntity.getUrl();
        ret.setOfflineType(vkDocumentEntity.getOfflineType());

        if (vkDocumentEntity.getDownloadRequest() != null)
            ret.setRequest(mapper.transform(vkDocumentEntity.getDownloadRequest()));
        return ret;
    }

    @Override
    public VKDocumentEntity transformInv(VkDocument vkDoc) {
        VKDocumentEntity vk = new VKDocumentEntity();
        vk.setId(vkDoc.getId());
        vk.setTitle(vkDoc.title);
        vk.setOwnerId(vkDoc.owner_id);
        vk.setSize(vkDoc.size);
        vk.setUrl(vkDoc.url);
        vk.setOfflineType(vkDoc.getOfflineType());
        return vk;
    }
}
