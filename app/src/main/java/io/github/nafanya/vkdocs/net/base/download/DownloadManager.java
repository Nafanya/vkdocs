package io.github.nafanya.vkdocs.net.base.download;

import java.util.List;

import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;

public interface DownloadManager {

    void enqueue(DownloadRequest request);
    void cancelRequest(DownloadRequest request);
    List<DownloadRequest> getQueue();
}
