package io.github.nafanya.vkdocs.data.database.mapper;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.DownloadRequestEntity;
import io.github.nafanya.vkdocs.data.database.model.VKDocumentEntity;
import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;
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
        ret.extType = vkDocumentEntity.getExtType();
        ret.setOfflineType(vkDocumentEntity.getOfflineType());
        ret.ext = vkDocumentEntity.getExt();
        ret.setPath(vkDocumentEntity.getPath());

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
        vk.setExtType(vkDoc.extType);
        vk.setOfflineType(vkDoc.getOfflineType());
        vk.setExt(vkDoc.getExt());
        vk.setPath(vkDoc.getPath());
        return vk;
    }
}
