package io.github.nafanya.vkdocs.domain.download;

import io.github.nafanya.vkdocs.domain.download.base.BaseDownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class DownloadRequest extends BaseDownloadRequest {
    private volatile boolean isCanceled;
    private DownloadManager.RequestObserver observer;
    private Scheduler scheduler = AndroidSchedulers.mainThread();
    private volatile boolean isActive;

    public DownloadRequest(String url, String destination) {
        super(url, destination);
    }

    public DownloadRequest(String url, String destination, DownloadManager.RequestObserver observer) {
        super(url, destination);
        this.observer = observer;
    }

    public DownloadRequest(String url, String dest, Scheduler scheduler, DownloadManager.RequestObserver observer) {
        super(url, dest);
        this.scheduler = scheduler;
        this.observer = observer;
    }

    public void setObserver(DownloadManager.RequestObserver observer) {
        this.observer = observer;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void cancel() {
        isActive = false;
        isCanceled = true;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public DownloadManager.RequestObserver getObserver() {
        return observer;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
