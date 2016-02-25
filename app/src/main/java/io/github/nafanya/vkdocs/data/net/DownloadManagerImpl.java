package io.github.nafanya.vkdocs.data.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.domain.DownloadManager;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

public class DownloadManagerImpl implements DownloadManager {
    private Scheduler workerScheduler;
    private List<Request> queue = new ArrayList<>();
    private int numDownloads;

    public DownloadManagerImpl(Scheduler workScheduler) {
        this.workerScheduler = workScheduler;
    }

    class Request extends AbstractRequest {
        private boolean isCanceled;
        private int id;

        public Request(String url, String destination, AbstractRequest.RequestObserver observer) {
            super(url, destination, observer);
        }

        @Override
        public void cancel() {
            isCanceled = true;
        }

        @Override
        public boolean isCanceled() {
            return isCanceled;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Request request = (Request) o;
            return id == request.id;

        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    @Override
    public void enqueue(final AbstractRequest abRequest) {
        if (!(abRequest instanceof Request))
            throw new AssertionError("request isn't instance of Request");

        final Request request = (Request)abRequest;
        queue.add(request);
        request.setId(++numDownloads);
        final AbstractRequest.RequestObserver callback = request.getObserver();

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(request.getUrl());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        queue.remove(request);
                        subscriber.onError(new AssertionError("Server returned HTTP " +
                                connection.getResponseCode() + " " +
                                connection.getResponseMessage()));
                        return;
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();
                    // download the file
                    InputStream input = connection.getInputStream();
                    try {
                        OutputStream output = new FileOutputStream(request.getDestination());
                        try {
                            byte data[] = new byte[4096];
                            long total = 0;
                            int count, prev = 0;
                            while ((count = input.read(data)) != -1) {
                                // allow canceling with back button
                                if (request.isCanceled()) {
                                    queue.remove(request);
                                    input.close();
                                    return;
                                }
                                total += count;
                                output.write(data, 0, count);
                                if (fileLength > 0) {
                                    int perc = (int) (total * 100 / fileLength);
                                    if (perc != prev)
                                        subscriber.onNext(perc);
                                    prev = perc;
                                } else {
                                    if (prev != -1)
                                        subscriber.onNext(-1);
                                    prev = -1;
                                }
                            }
                            queue.remove(request);
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
