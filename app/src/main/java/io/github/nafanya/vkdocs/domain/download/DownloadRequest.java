package io.github.nafanya.vkdocs.domain.download;

import io.github.nafanya.vkdocs.domain.download.base.BaseDownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class DownloadRequest extends BaseDownloadRequest {
    private volatile boolean isCanceled;
    private volatile DownloadManager.RequestObserver observer;
    private Scheduler observeScheduler = AndroidSchedulers.mainThread();
    private volatile boolean isActive;

    public DownloadRequest() {
        super(null, null);
    }

    public DownloadRequest(String url, String destination) {
        super(url, destination);
    }

    public DownloadRequest(String url, String destination, DownloadManager.RequestObserver observer) {
        super(url, destination);
        this.observer = observer;
    }

    public DownloadRequest(String url, String dest, Scheduler observeScheduler, DownloadManager.RequestObserver observer) {
        super(url, dest);
        this.observeScheduler = observeScheduler;
        this.observer = observer;
    }

    public void setObserver(DownloadManager.RequestObserver observer) {
        this.observer = observer;
    }

    public void setObserveScheduler(Scheduler observeScheduler) {
        this.observeScheduler = observeScheduler;
    }

    public void cancel() {
        isCanceled = true;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public DownloadManager.RequestObserver getObserver() {
        return observer;
    }

    public Scheduler getObserveScheduler() {
        return observeScheduler;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
