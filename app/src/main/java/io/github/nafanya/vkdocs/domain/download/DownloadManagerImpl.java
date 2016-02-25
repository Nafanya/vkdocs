package io.github.nafanya.vkdocs.domain.download;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class DownloadManagerImpl implements DownloadManager {
    private Scheduler workerScheduler;
    private List<Request> queue = new ArrayList<>();
    private int numDownloads;
    private RequestStorage storage;

    class Request extends BaseRequest {
        private volatile boolean isCanceled;

        private RequestObserver observer;
        private Scheduler scheduler = AndroidSchedulers.mainThread();

        public Request(String url, String destination, BaseRequest.RequestObserver observer) {
            super(url, destination);
            this.observer = observer;
        }

        public Request(String url, String dest, Scheduler scheduler, RequestObserver observer) {
            super(url, dest);
            this.scheduler = scheduler;
            this.observer = observer;
        }

        public void cancel() {
            isCanceled = true;
        }

        public boolean isCanceled() {
            return isCanceled;
        }

        public RequestObserver getObserver() {
            return observer;
        }

        public Scheduler getScheduler() {
            return scheduler;
        }
    }


    public DownloadManagerImpl(Scheduler workScheduler, RequestStorage storage) {
        this.workerScheduler = workScheduler;
        this.storage = storage;
    }

    @Override
    public void enqueue(final BaseRequest abRequest) {
        if (!(abRequest instanceof Request))
            throw new AssertionError("request isn't instance of DownloadRequest");

        final Request request = (Request)abRequest;
        queue.add(request);
        request.setId(++numDownloads);
        final BaseRequest.RequestObserver callback = request.getObserver();

        Observable.create(new Observable.OnSubscribe<Integer>() {
            private int fileLength;
            private int prevPercentage;

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(request.getUrl());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Range", "bytes=" + request.getBytes() + "-" );
                    connection.connect();
                    // expect HTTP 206 Partial content, so we don't mistakenly save error report instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
                        queue.remove(request);
                        subscriber.onError(new AssertionError("Server returned HTTP " +
                                connection.getResponseCode() + " " +
                                connection.getResponseMessage()));
                        return;
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    fileLength = connection.getContentLength();
                    // download the file
                    InputStream input = connection.getInputStream();
                    try {
                        OutputStream output = new FileOutputStream(request.getDest());
                        try {
                            byte data[] = new byte[4096];
                            long total = 0;
                            int count;
                            while ((count = input.read(data)) != -1) {
                                // allow canceling with back button
                                if (request.isCanceled()) {
                                    queue.remove(request);
                                    input.close();
                                    return;
                                }
                                total += count;
                                output.write(data, 0, count);
                                storage.update(request);
                                publishProgress(subscriber, total);
                            }
                            queue.remove(request);
                            storage.delete(request);
                            subscriber.onCompleted();
                        } finally {
                            output.close();
                        }
                    } finally {
                        input.close();
                    }
                } catch (Exception e) {
                    queue.remove(request);
                    subscriber.onError(e);
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }

            private void publishProgress(Subscriber<? super Integer> subscriber, long total) {
                if (fileLength > 0) {
                    int perc = (int) (total * 100 / fileLength);
                    if (perc != prevPercentage)
                        subscriber.onNext(perc);
                    prevPercentage = perc;
                } else {
                    if (prevPercentage != -1)
                        subscriber.onNext(-1);
                    prevPercentage = -1;
                }
            }
        }).cache().subscribeOn(workerScheduler).observeOn(request.getScheduler()).
        subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                if (callback != null)
                    callback.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                if (callback != null)
                    callback.onError((Exception) e);
            }

            @Override
            public void onNext(Integer progress) {
                if (callback != null) {
                    if (progress == -1)
                        callback.onInfiniteProgress();
                    else
                        callback.onProgress(progress);
                }
            }
        });
    }
}
