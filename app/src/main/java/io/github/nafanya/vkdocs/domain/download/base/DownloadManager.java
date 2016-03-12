package io.github.nafanya.vkdocs.domain.download.base;

import java.util.List;

public interface DownloadManager {
    interface RequestObserver {
        void onProgress(int percentage);
        void onComplete();
        void onError(Exception e);
        void onInfiniteProgress();
    }

    void enqueue(DownloadRequest request);
    void cancelRequest(DownloadRequest request);
    List<DownloadRequest> getQueue();
}
