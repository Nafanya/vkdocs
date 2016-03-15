package io.github.nafanya.vkdocs.domain.download.base;

import java.util.List;

public interface DownloadManager {

    void enqueue(DownloadRequest request);
    void cancelRequest(DownloadRequest request);
    List<DownloadRequest> getQueue();
}
