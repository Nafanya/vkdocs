package io.github.nafanya.vkdocs.domain;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public interface DownloadManager {
    abstract class AbstractRequest {

        public Scheduler getScheduler() {
            return scheduler;
        }

        public interface RequestObserver {
            void onProgress(int percentage);
            void onComplete();
            void onError(Exception e);
            void onInfiniteProgress();
        }

        private String url;
        private String destination;
        private RequestObserver observer;
        private Scheduler scheduler = AndroidSchedulers.mainThread();

        public AbstractRequest(String url, String destination) {
            this.url = url;
            this.destination = destination;
        }

        public AbstractRequest(String url, String destination, Scheduler scheduler, RequestObserver observer) {
            this.scheduler = scheduler;
            this.url = url;
            this.destination = destination;
            this.observer = observer;
        }

        public AbstractRequest(String url, String destination, RequestObserver observer) {
            this.url = url;
            this.destination = destination;
            this.observer = observer;
        }

        public String getUrl() {
            return url;
        }

        public String getDestination() {
            return destination;
        }

        public RequestObserver getObserver() {
            return observer;
        }

        public abstract void cancel();
        public abstract boolean isCanceled();
    }

    void enqueue(AbstractRequest abstractRequest);
}
