package io.github.nafanya.vkdocs.domain.download;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.download.base.RequestStorage;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import timber.log.Timber;

public class InterruptableDownloadManager implements DownloadManager<DownloadRequest> {
    private Scheduler workerScheduler;
    private List<DownloadRequest> queue = new ArrayList<>();
    private int numDownloads;
    private RequestStorage<DownloadRequest> storage;

    public InterruptableDownloadManager(Scheduler workScheduler, RequestStorage<DownloadRequest> storage) {
        this.workerScheduler = workScheduler;
        this.storage = storage;
        queue = storage.getAll();
        Timber.d("queue size %d", queue.size());
        for (int i = 0; i < queue.size(); ++i)
            Timber.d("down record " + queue.get(i).getUrl() + " bytes: " + queue.get(i).getBytes() + " from " + queue.get(i).getTotalBytes());
    }

    //public final int SAVE_EVERY_BYTES = 1024 * 1024; //bytes

    //TODO remove from storage when canceled and something error
    @Override
    public void enqueue(final DownloadRequest request) {
        queue.add(request);
        request.setId(++numDownloads);
        final RequestObserver callback = request.getObserver();

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
                    Timber.d("Server returned HTTP " +
                            connection.getResponseCode() + " " +
                            connection.getResponseMessage());

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL &&
                        connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        queue.remove(request);
                        subscriber.onError(new RuntimeException("Server returned HTTP " +
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
                                    //remove from table
                                    return;
                                }
                                total += count;
                                output.write(data, 0, count);
                                storage.update(request);
                                publishProgress(subscriber, total);
                            }
                            Timber.d("count = " + count);
                            Timber.d("total = " + total);
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

    @Override
    public List<DownloadRequest> getQueue() {
        return queue;
    }
}
