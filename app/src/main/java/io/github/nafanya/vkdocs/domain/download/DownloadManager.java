package io.github.nafanya.vkdocs.domain.download;

public interface DownloadManager {

    void enqueue(BaseRequest request);
}
