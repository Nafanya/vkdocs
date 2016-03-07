package io.github.nafanya.vkdocs.data.database.mapper;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.DownloadRequestEntity;
import io.github.nafanya.vkdocs.domain.download.DownloadRequest;

public class DownloadRequestMapper extends
        Mapper<DownloadRequestEntity, DownloadRequest> {
    @Override
    public DownloadRequest transform(DownloadRequestEntity x) {
        DownloadRequest ret = new DownloadRequest(x.getUrl(), x.getDest());
        ret.setId(x.getId());
        ret.setTotalBytes(x.getTotalBytes());
        ret.setBytes(x.getBytes());
        return ret;
    }

    @Override
    public List<DownloadRequest> transform(List<DownloadRequestEntity> x) {
        List<DownloadRequest> ret = new ArrayList<>();
        for (int i = 0; i < x.size(); ++i)
            ret.add(transform(x.get(i)));
        return ret;
    }

    @Override
    public DownloadRequestEntity transformInv(DownloadRequest x) {
        DownloadRequestEntity ret = new DownloadRequestEntity();
        ret.setId(x.getId());
        ret.setUrl(x.getUrl());
        ret.setBytes(x.getBytes());
        ret.setTotalBytes(x.getTotalBytes());
        ret.setDest(x.getDest());
        return ret;
    }
}
