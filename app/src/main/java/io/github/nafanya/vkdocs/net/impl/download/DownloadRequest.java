package io.github.nafanya.vkdocs.net.impl.download;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.net.base.download.BaseDownloadRequest;
import rx.Subscription;

public class DownloadRequest extends BaseDownloadRequest {
    private volatile boolean isCanceled;
    private volatile boolean isActive;
    private List<RequestListener> listeners = new ArrayList<>();
    private List<Subscription> subscriptions = new ArrayList<>();

    public DownloadRequest() {
        super(null, null);
    }

    public DownloadRequest(String url, String destination) {
        super(url, destination);
    }

    public Subscription addListener(RequestListener listener) {
        Subscription subscription = new Subscription() {
            private boolean isUnsub = false;

            @Override
            public void unsubscribe() {
                isUnsub = true;
                int ind = listeners.indexOf(listener);
                listeners.remove(ind);
                subscriptions.remove(ind);
            }

            @Override
            public boolean isUnsubscribed() {
                return isUnsub;
            }
        };

        listeners.add(listener);
        subscriptions.add(subscription);

        if (isCompleted)
            listener.onComplete();
        else if (lastPer != null)
            listener.onProgress(lastPer);
        else if (lastEx != null)
            listener.onError(lastEx);
        return subscription;
    }

    public void removeListener(RequestListener listener) {
        int ind = listeners.indexOf(listener);
        subscriptions.get(ind).unsubscribe();
    }

    public void cancel() {
        isCanceled = true;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    /***Holy shit code, I hate Observers and Subscribers, so weak***/
    private Exception lastEx;
    private Integer lastPer;
    private boolean isCompleted;

    public void publishProgress(int perc) {
        if (isCompleted)
            throw new IllegalStateException("cant publish progress");
        lastEx = null;
        isCompleted = false;
        lastPer = perc;
        for (RequestListener l: listeners)
            l.onProgress(lastPer);
    }

    public void publishComplete() {
        isCompleted = true;
        for (RequestListener l: listeners)
            l.onComplete();
    }

    public void publishError(Exception e) {
        lastEx = e;
        lastPer = null;
        for (RequestListener l: listeners)
            l.onError(e);
    }

    public void resetError() {
        lastEx = null;
    }

    public interface RequestListener {
        void onProgress(int percentage);
        void onComplete();
        void onError(Exception e);
    }
}
