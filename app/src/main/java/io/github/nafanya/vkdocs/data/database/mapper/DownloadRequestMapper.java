package io.github.nafanya.vkdocs.data.database.mapper;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.DownloadRequestEntity;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;

public class DownloadRequestMapper extends Mapper<DownloadRequestEntity, DownloadRequest> {
    @Override
    public DownloadRequest transform(DownloadRequestEntity x) {
        DownloadRequest ret = new DownloadRequest();
        ret.setId(x.getId());
        ret.setUrl(x.getUrl());
        ret.setDest(x.getDest());
        ret.setBytes(x.getBytes());
        ret.setTotalBytes(x.getTotalBytes());
        ret.setDocId(x.getDocId());
        return ret;
    }


    @Override
    public DownloadRequestEntity transformInv(DownloadRequest x) {
        DownloadRequestEntity ret = new DownloadRequestEntity();
        ret.setId(x.getId());
        ret.setUrl(x.getUrl());
        ret.setDest(x.getDest());
        ret.setBytes(x.getBytes());
        ret.setTotalBytes(x.getTotalBytes());
        ret.setDocId(x.getDocId());
        return ret;
    }
}
