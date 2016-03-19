package io.github.nafanya.vkdocs.net.base.download;

import java.util.List;

import io.github.nafanya.vkdocs.net.base.download.BaseDownloadRequest;

public interface RequestStorage<T extends BaseDownloadRequest> {
    /*
    DownloadRequestEntity:
        id
        url
        dest
        bytes
        total_bytes
    */

    List<T> getAll();
    void update(T request);
    void insert(T request);
    void delete(T request);
}
