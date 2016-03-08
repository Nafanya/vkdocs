package io.github.nafanya.vkdocs.domain.download.base;

import java.util.List;

public interface DownloadManager<T extends BaseDownloadRequest> {
    interface RequestObserver {
        void onProgress(int percentage);
        void onComplete();
        void onError(Exception e);
        void onInfiniteProgress();
    }

    void enqueue(T request);
    void cancelRequest(T request);
    List<T> getQueue();
}
