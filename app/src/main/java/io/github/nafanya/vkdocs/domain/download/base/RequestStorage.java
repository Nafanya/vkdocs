package io.github.nafanya.vkdocs.domain.download.base;

import java.util.List;

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
    void add(T request);
    void delete(T request);
}
